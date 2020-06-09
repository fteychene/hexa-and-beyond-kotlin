package beyondhexa.hexa.infra.adapter

import arrow.core.Either
import arrow.core.compose
import arrow.core.right
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.typeclasses.Monoid
import beyondhexa.hexa.domain.model.AudienceError
import beyondhexa.hexa.domain.model.Tracker
import beyondhexa.hexa.domain.model.counter
import beyondhexa.hexa.domain.model.ips
import beyondhexa.hexa.domain.port.secondary.AudiencePort
import java.util.concurrent.atomic.AtomicReference

class TrackerMonoid : Monoid<Tracker> {
    override fun empty(): Tracker = Tracker()

    override fun Tracker.combine(b: Tracker): Tracker =
            ((Tracker.counter.lift { it + b.counter } compose
                    Tracker.ips.lift { (it + b.ips).toSet().toList() }))(this)
}

class InMemoryAudience : AudiencePort, Monoid<Tracker> by TrackerMonoid() {

    var audience = AtomicReference(empty())

    override fun load(): IO<Either<AudienceError, Tracker>> = IO.fx { audience.get().right() }

    override fun save(tracker: Tracker): IO<Either<AudienceError, Unit>> =
            IO.fx {
                audience.getAndUpdate { original -> original.combine(tracker) }
                Unit.right()
            }


}