package beyondhexa.di.infra.reactor.adapter

import arrow.core.Either
import arrow.core.right
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.k
import arrow.mtl.EitherT
import beyondhexa.di.domain.model.Ip
import beyondhexa.di.domain.model.SecurityError
import reactor.core.publisher.Flux

class FluxInMemorySecurity{

    val denyList = IntRange(0, 30).map { "192.168.10.$it" }.toList()

    fun allowed(ip: Ip): EitherT<ForFluxK, SecurityError, Boolean> =
            EitherT(Flux.just(denyList.contains(ip).not().right() as Either<SecurityError, Boolean>).k())

}