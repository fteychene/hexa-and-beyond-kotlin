package beyondhexa.mtl.infra.rx.adapter

import arrow.core.right
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.k
import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.LoggerError
import beyondhexa.mtl.domain.port.secondary.LoggerPort
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun threadName(): String =
        Thread.currentThread().name

class ConsoleLogger : LoggerPort<ForSingleK> {
    override fun log(message: String): EitherT<ForSingleK, LoggerError, Unit> =
            EitherT(
                    Single.just(println("[${threadName()}] $message"))
                            .delay(Random.nextLong(0, 200), TimeUnit.MILLISECONDS)
                            .map { it.right() }
                            .k())
}