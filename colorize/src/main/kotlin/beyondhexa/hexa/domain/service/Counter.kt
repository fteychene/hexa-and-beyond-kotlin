package beyondhexa.hexa.domain.service

import arrow.core.*
import arrow.core.extensions.fx
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.fix
import beyondhexa.hexa.domain.model.*
import beyondhexa.hexa.domain.port.primary.CounterPort
import beyondhexa.hexa.domain.port.secondary.AudiencePort
import beyondhexa.hexa.domain.port.secondary.LoggerPort
import beyondhexa.hexa.domain.port.secondary.SecurityAllowerPort

class Counter(
        val audience: AudiencePort,
        val security: SecurityAllowerPort,
        val logger: LoggerPort
) : CounterPort {

    // Compose IO and Either manually

    fun <E, A, B> IO<Either<E, A>>.mapC(next: (A) -> Either<E, B>) = map { it.map(next) }

    fun <E, A, B> IO<Either<E, A>>.flatMapC(next: (A) -> IO<Either<E, B>>) = flatMap {
        when(it) {
            is Either.Left -> IO.just(it)
            is Either.Right -> next(it.b)
        }
    }

    // Monad does not compose naturally so compose by hand
    override fun visit(sourceIp: Ip): IO<Either<CounterError, Int>> =
            logger.log("Checking if ip is allowed")
                    .flatMapC { security.allowed(sourceIp) }
                    .map { it.filterOrElse(::identity) { UnauthorizedIp(sourceIp) } }
                    .flatMapC { audience.load() }
                    .flatMapC { tracker ->
                        IO.fx {
                            !logger.log("Tracking visit")
                            !logger.log("Save new tracking data")
                            !audience.save(Tracker(counter = 1, ips = listOf(sourceIp)))
                            tracker.counter.right()
                        }.fix()
                    }
}