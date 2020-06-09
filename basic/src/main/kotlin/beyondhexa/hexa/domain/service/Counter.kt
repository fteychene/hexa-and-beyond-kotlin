package beyondhexa.hexa.domain.service

import arrow.core.Either
import arrow.core.compose
import arrow.core.extensions.fx
import arrow.core.maybe
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

    fun trackerUpdater(sourceIp: Ip): (Tracker) -> Tracker =
            Tracker.counter.lift { it + 1 } compose
                    Tracker.ips.lift { ips -> if (ips.contains(sourceIp)) ips else ips + sourceIp }

    override fun visit(sourceIp: Ip): Either<CounterError, Int> =
            Either.fx {
                logger.log("Checking if ip is allowed")
                val allowed = !security.allowed(sourceIp)
                val (tracker) = allowed.maybe { Unit }
                        .toEither { UnauthorizedIp(sourceIp) }
                        .flatMap { audience.load() }
                logger.log("Tracking visit")
                val updatedTracker = trackerUpdater(sourceIp)(tracker)
                logger.log("Save updated tracker")
                audience.save(updatedTracker)
                updatedTracker.counter
            }
}