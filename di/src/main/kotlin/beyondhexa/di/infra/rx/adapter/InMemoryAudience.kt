package beyondhexa.di.infra.rx.adapter

import arrow.core.compose
import arrow.core.right
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.k
import arrow.mtl.EitherT
import arrow.typeclasses.Monoid
import beyondhexa.di.domain.model.AudienceError
import beyondhexa.di.domain.model.Tracker
import beyondhexa.di.domain.model.counter
import beyondhexa.di.domain.model.ips
import io.reactivex.Single
import java.util.concurrent.atomic.AtomicReference

class TrackerMonoid : Monoid<Tracker> {
    override fun empty(): Tracker = Tracker()

    override fun Tracker.combine(b: Tracker): Tracker =
            ((Tracker.counter.lift { it + b.counter } compose
                    Tracker.ips.lift { (it + b.ips).toSet().toList() }))(this)
}

class InMemoryAudience : Monoid<Tracker> by TrackerMonoid() {

    var audience = AtomicReference(empty())

    fun load(): EitherT<ForSingleK, AudienceError, Tracker> = EitherT(Single.just(audience.get().right()).k())

    fun save(tracker: Tracker): EitherT<ForSingleK, AudienceError, Unit> = EitherT(
            Single.defer {
                audience.getAndUpdate { original -> original.combine(tracker) }
                Single.just(Unit.right())
            }.k()
    )

}