package beyondhexa.di.infra.io.adapter

import arrow.core.compose
import arrow.core.right
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.mtl.EitherT
import arrow.typeclasses.Monoid
import beyondhexa.di.domain.model.AudienceError
import beyondhexa.di.domain.model.Tracker
import beyondhexa.di.domain.model.counter
import beyondhexa.di.domain.model.ips
import java.util.concurrent.atomic.AtomicReference

class TrackerMonoid : Monoid<Tracker> {
    override fun empty(): Tracker = Tracker()

    override fun Tracker.combine(b: Tracker): Tracker =
            ((Tracker.counter.lift { it + b.counter } compose
                    Tracker.ips.lift { (it + b.ips).toSet().toList() }))(this)
}

class InMemoryAudience: Monoid<Tracker> by TrackerMonoid() {

    var audience = AtomicReference(empty())

    fun load(): EitherT<ForIO, AudienceError, Tracker> = EitherT(IO.fx { audience.get().right() })

    fun save(tracker: Tracker): EitherT<ForIO, AudienceError, Unit> = EitherT(
            IO.fx {
                audience.getAndUpdate { original -> original.combine(tracker) }
                Unit.right()
            }
    )

}