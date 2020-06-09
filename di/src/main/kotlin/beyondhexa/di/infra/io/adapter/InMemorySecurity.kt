package beyondhexa.di.infra.io.adapter

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.mtl.EitherT
import beyondhexa.di.domain.model.Ip
import beyondhexa.di.domain.model.SecurityError

class InMemorySecurity {

    val denyList = IntRange(0, 30).map { "192.168.10.$it" }.toList()

    fun allowed(ip: Ip): EitherT<ForIO, SecurityError, Boolean> = EitherT.just(IO.applicative(), denyList.contains(ip).not())

}