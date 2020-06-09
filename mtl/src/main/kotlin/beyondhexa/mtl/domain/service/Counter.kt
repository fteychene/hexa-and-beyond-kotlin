package beyondhexa.mtl.domain.service

import arrow.core.maybe
import arrow.fx.typeclasses.MonadDefer
import arrow.mtl.EitherT
import arrow.mtl.extensions.eithert.monad.monad
import arrow.mtl.fix
import beyondhexa.mtl.domain.model.CounterError
import beyondhexa.mtl.domain.model.Ip
import beyondhexa.mtl.domain.model.Tracker
import beyondhexa.mtl.domain.model.UnauthorizedIp
import beyondhexa.mtl.domain.port.primary.CounterPort
import beyondhexa.mtl.domain.port.secondary.AudiencePort
import beyondhexa.mtl.domain.port.secondary.LoggerPort
import beyondhexa.mtl.domain.port.secondary.SecurityAllowerPort

class Counter<F>(
        async: MonadDefer<F>,
        val audience: AudiencePort<F>,
        val security: SecurityAllowerPort<F>,
        val logger: LoggerPort<F>
) : CounterPort<F>, MonadDefer<F> by async {

    override fun visit(sourceIp: Ip): EitherT<F, CounterError, Int> =
            let { async ->
                EitherT.monad<F, CounterError>(async).fx.monad {
                    logger.log("Checking if ip is allowed").bind()
                    val (allowed) = security.allowed(sourceIp)
                    EitherT.fromEither(async, allowed.maybe { Unit }.toEither { UnauthorizedIp(sourceIp) }).bind()
                    val (tracker) = audience.load()
                    logger.log("Tracking visit").bind()
                    logger.log("Save new tracking data").bind()
                    audience.save(Tracker(counter = 1, ips = listOf(sourceIp))).bind()
                    tracker.counter
                }.fix()
            }
}