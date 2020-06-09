package beyondhexa.di.infra.reactor.adapter

import arrow.core.right
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.k
import arrow.mtl.EitherT
import beyondhexa.di.domain.model.LoggerError
import reactor.core.publisher.toMono
import kotlin.random.Random


class MonoConsoleLogger {

    fun log(message: String): EitherT<ForMonoK, LoggerError, Unit> =
            EitherT(
                    println("[${threadName()}] $message")
                            .toMono()
                            .delayElement(java.time.Duration.ofMillis(Random.nextLong(0, 200)))
                            .map { it.right() }
                            .k())
}