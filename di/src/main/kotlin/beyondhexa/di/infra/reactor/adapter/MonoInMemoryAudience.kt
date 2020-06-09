package beyondhexa.di.infra.reactor.adapter

import arrow.core.right
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.k
import arrow.mtl.EitherT
import arrow.typeclasses.Monoid
import beyondhexa.di.domain.model.AudienceError
import beyondhexa.di.domain.model.Tracker
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.concurrent.atomic.AtomicReference

class MonoInMemoryAudience : Monoid<Tracker> by TrackerMonoid() {

    var audience = AtomicReference(empty())

    fun load(): EitherT<ForMonoK, AudienceError, Tracker> = EitherT(Mono.just(audience.get().right()).k())

    fun save(tracker: Tracker): EitherT<ForMonoK, AudienceError, Unit> = EitherT(
            Mono.defer {
                audience.getAndUpdate { original -> original.combine(tracker) }
                Unit.right().toMono()
            }.k()
    )

}