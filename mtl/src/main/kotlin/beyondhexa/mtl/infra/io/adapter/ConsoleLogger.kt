package beyondhexa.mtl.infra.io.adapter

import arrow.core.right
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.typeclasses.Duration
import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.LoggerError
import beyondhexa.mtl.domain.port.secondary.LoggerPort
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun threadName(): String =
        Thread.currentThread().name

class ConsoleLogger : LoggerPort<ForIO> {
    override fun log(message: String): EitherT<ForIO, LoggerError, Unit> =
            EitherT(
                    IO.sleep(Duration(Random.nextLong(0, 200), TimeUnit.MILLISECONDS))
                            .map { println("[${threadName()}] $message").right() })
}