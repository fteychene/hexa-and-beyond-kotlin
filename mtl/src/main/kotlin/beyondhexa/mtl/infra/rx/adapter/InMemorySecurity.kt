package beyondhexa.mtl.infra.rx.adapter

import arrow.core.Either
import arrow.core.right
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.k
import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.Ip
import beyondhexa.mtl.domain.model.SecurityError
import beyondhexa.mtl.domain.port.secondary.SecurityAllowerPort
import io.reactivex.Single

class InMemorySecurity : SecurityAllowerPort<ForSingleK> {

    val denyList = IntRange(0, 30).map { "192.168.10.$it" }.toList()

    override fun allowed(ip: Ip): EitherT<ForSingleK, SecurityError, Boolean> =
            EitherT(Single.just(denyList.contains(ip).not().right() as Either<SecurityError, Boolean>).k())

}