package beyondhexa.di.domain.port

import arrow.core.Either
import arrow.fx.typeclasses.MonadDefer
import arrow.mtl.ReaderT
import beyondhexa.di.domain.model.CounterError
import beyondhexa.di.domain.model.Ip

data class VisitDependencies<F>(
        val async: MonadDefer<F>,
        val audienceLoader: AudienceLoader<F>,
        val trackerUpdater: TrackerUpdate<F>,
        val securityIpAllower: SecurityIpAllower<F>,
        val logger: Logger<F>
)

typealias Visit<F> = (Ip) -> ReaderT<F, VisitDependencies<F>, Either<CounterError, String>>