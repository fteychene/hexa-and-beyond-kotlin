package beyondhexa.mtl.infra.io.adapter

import arrow.core.right
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.mtl.EitherT
import arrow.typeclasses.Monoid
import beyondhexa.mtl.domain.model.AudienceError
import beyondhexa.mtl.domain.model.Tracker
import beyondhexa.mtl.domain.model.counter
import beyondhexa.mtl.domain.model.ips
import beyondhexa.mtl.domain.port.secondary.AudiencePort
import java.util.concurrent.atomic.AtomicReference

class TrackerMonoid : Monoid<Tracker> {
    override fun empty(): Tracker = Tracker()

    override fun Tracker.combine(b: Tracker): Tracker =
            ((Tracker.counter.lift { it + b.counter } compose
                    Tracker.ips.lift { (it + b.ips).toSet().toList() }))(this)
}

class InMemoryAudience : AudiencePort<ForIO>, Monoid<Tracker> by TrackerMonoid() {

    var audience = AtomicReference(empty())

    override fun load(): EitherT<ForIO, AudienceError, Tracker> = EitherT(IO.fx { audience.get().right() })

    override fun save(tracker: Tracker): EitherT<ForIO, AudienceError, Unit> = EitherT(
            IO.fx {
                audience.getAndUpdate { original -> original.combine(tracker) }
                Unit.right()
            }
    )

}