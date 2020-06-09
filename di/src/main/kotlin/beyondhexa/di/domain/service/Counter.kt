package beyondhexa.di.domain.service

import arrow.core.maybe
import arrow.mtl.EitherT
import arrow.mtl.ReaderT
import arrow.mtl.extensions.eithert.monad.monad
import arrow.mtl.fix
import beyondhexa.di.domain.port.VisitDependencies
import beyondhexa.di.domain.model.CounterError
import beyondhexa.di.domain.model.Ip
import beyondhexa.di.domain.model.Tracker
import beyondhexa.di.domain.model.UnauthorizedIp

fun <F> visit(sourceIp: Ip) = ReaderT { ctx: VisitDependencies<F> ->
    EitherT.monad<F, CounterError>(ctx.async).fx.monad {
        !ctx.logger("Checking if ip is allowed")
        val allowed = !ctx.securityIpAllower(sourceIp)
        !EitherT.fromEither(ctx.async, allowed.maybe { Unit }.toEither { UnauthorizedIp(sourceIp) })
        val tracker = !ctx.audienceLoader()
        !ctx.logger("Tracking visit")
        !ctx.logger("Save new tracking data")
        !ctx.trackerUpdater(Tracker(counter = 1, ips = listOf(sourceIp)))
        tracker.counter
    }.fix().value()
}