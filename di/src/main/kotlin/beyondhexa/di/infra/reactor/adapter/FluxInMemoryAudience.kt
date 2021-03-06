package beyondhexa.di.infra.reactor.adapter

import arrow.core.compose
import arrow.core.right
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.k
import arrow.mtl.EitherT
import arrow.typeclasses.Monoid
import beyondhexa.di.domain.model.AudienceError
import beyondhexa.di.domain.model.Tracker
import beyondhexa.di.domain.model.counter
import beyondhexa.di.domain.model.ips
import reactor.core.publisher.Flux
import reactor.core.publisher.toMono
import java.util.concurrent.atomic.AtomicReference

class TrackerMonoid : Monoid<Tracker> {
    override fun empty(): Tracker = Tracker()

    override fun Tracker.combine(b: Tracker): Tracker =
            ((Tracker.counter.lift { it + b.counter } compose
                    Tracker.ips.lift { (it + b.ips).toSet().toList() }))(this)
}

class FluxInMemoryAudience : Monoid<Tracker> by TrackerMonoid() {

    var audience = AtomicReference(empty())

    fun load(): EitherT<ForFluxK, AudienceError, Tracker> = EitherT(Flux.just(audience.get().right()).k())

    fun save(tracker: Tracker): EitherT<ForFluxK, AudienceError, Unit> = EitherT(
            Flux.defer {
                audience.getAndUpdate { original -> original.combine(tracker) }
                Unit.right().toMono()
            }.k()
    )

}