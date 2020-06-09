package beyondhexa.di.infra.rx.adapter

import arrow.core.Either
import arrow.core.right
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.k
import arrow.mtl.EitherT
import beyondhexa.di.domain.model.Ip
import beyondhexa.di.domain.model.SecurityError
import io.reactivex.Single

class InMemorySecurity {

    val denyList = IntRange(0, 30).map { "192.168.10.$it" }.toList()

    fun allowed(ip: Ip): EitherT<ForSingleK, SecurityError, Boolean> =
            EitherT(Single.just(denyList.contains(ip).not().right() as Either<SecurityError, Boolean>).k())

}