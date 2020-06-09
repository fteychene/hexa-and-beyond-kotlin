package beyondhexa.hexa.domain.port.secondary

import arrow.core.Either
import arrow.fx.IO
import beyondhexa.hexa.domain.model.AudienceError
import beyondhexa.hexa.domain.model.Tracker

interface AudiencePort {

    fun load(): IO<Either<AudienceError, Tracker>>

    fun save(tracker: Tracker): IO<Either<AudienceError, Unit>>
}