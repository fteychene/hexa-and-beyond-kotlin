package beyondhexa.mtl.infra.rx.adapter

import arrow.core.right
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.k
import arrow.mtl.EitherT
import arrow.typeclasses.Monoid
import beyondhexa.mtl.domain.model.AudienceError
import beyondhexa.mtl.domain.model.Tracker
import beyondhexa.mtl.domain.model.counter
import beyondhexa.mtl.domain.model.ips
import beyondhexa.mtl.domain.port.secondary.AudiencePort
import io.reactivex.Single
import java.util.concurrent.atomic.AtomicReference

class TrackerMonoid : Monoid<Tracker> {
    override fun empty(): Tracker = Tracker()

    override fun Tracker.combine(b: Tracker): Tracker =
            ((Tracker.counter.lift { it + b.counter } compose
                    Tracker.ips.lift { (it + b.ips).toSet().toList() }))(this)
}

class InMemoryAudience : AudiencePort<ForSingleK>, Monoid<Tracker> by TrackerMonoid() {

    var audience = AtomicReference(empty())

    override fun load(): EitherT<ForSingleK, AudienceError, Tracker> = EitherT(Single.just(audience.get().right()).k())

    override fun save(tracker: Tracker): EitherT<ForSingleK, AudienceError, Unit> = EitherT(
            Single.defer {
                audience.getAndUpdate { original -> original.combine(tracker) }
                Single.just(Unit.right())
            }.k()
    )

}