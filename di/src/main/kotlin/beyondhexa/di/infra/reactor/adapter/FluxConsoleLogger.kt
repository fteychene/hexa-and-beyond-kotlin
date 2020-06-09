package beyondhexa.di.infra.reactor.adapter

import arrow.core.right
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.k
import arrow.mtl.EitherT
import beyondhexa.di.domain.model.LoggerError
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import kotlin.random.Random

fun threadName(): String =
        Thread.currentThread().name

class FluxConsoleLogger {
    fun log(message: String): EitherT<ForFluxK, LoggerError, Unit> =
            EitherT(
                    println("[${threadName()}] $message")
                            .toMono().toFlux()
                            .delayElements(java.time.Duration.ofMillis(Random.nextLong(0, 200)))
                            .map { it.right() }
                            .k())
}