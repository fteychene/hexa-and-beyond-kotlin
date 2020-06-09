package beyondhexa.hexa.infra.adapter

import arrow.core.Either
import arrow.core.right
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.typeclasses.Duration
import beyondhexa.hexa.domain.model.LoggerError
import beyondhexa.hexa.domain.port.secondary.LoggerPort
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun threadName(): String =
        Thread.currentThread().name

class ConsoleLogger : LoggerPort {
    override fun log(message: String): IO<Either<LoggerError, Unit>> =
            IO.sleep(Duration(Random.nextLong(0, 200), TimeUnit.MILLISECONDS))
                            .map { println("[${threadName()}] $message").right() }
}