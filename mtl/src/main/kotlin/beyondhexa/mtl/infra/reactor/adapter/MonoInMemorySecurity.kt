package beyondhexa.mtl.infra.reactor.adapter

import arrow.core.Either
import arrow.core.right
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.k
import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.Ip
import beyondhexa.mtl.domain.model.SecurityError
import beyondhexa.mtl.domain.port.secondary.SecurityAllowerPort
import reactor.core.publisher.Mono

class MonoInMemorySecurity : SecurityAllowerPort<ForMonoK> {

    val denyList = IntRange(0, 30).map { "192.168.10.$it" }.toList()

    override fun allowed(ip: Ip): EitherT<ForMonoK, SecurityError, Boolean> =
            EitherT(Mono.just(denyList.contains(ip).not().right() as Either<SecurityError, Boolean>).k())

}