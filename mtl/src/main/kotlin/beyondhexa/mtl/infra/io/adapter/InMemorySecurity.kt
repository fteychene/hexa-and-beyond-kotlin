package beyondhexa.mtl.infra.io.adapter

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.Ip
import beyondhexa.mtl.domain.model.SecurityError
import beyondhexa.mtl.domain.port.secondary.SecurityAllowerPort

class InMemorySecurity : SecurityAllowerPort<ForIO> {

    val denyList = IntRange(0, 30).map { "192.168.10.$it" }.toList()

    override fun allowed(ip: Ip): EitherT<ForIO, SecurityError, Boolean> = EitherT.just(IO.applicative(), denyList.contains(ip).not())

}