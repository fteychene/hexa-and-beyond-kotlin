package beyondhexa.hexa.domain.port.secondary

import arrow.core.Either
import beyondhexa.hexa.domain.model.AudienceError
import beyondhexa.hexa.domain.model.Tracker

interface AudiencePort {

    fun load(): Either<AudienceError, Tracker>

    fun save(tracker: Tracker): Either<AudienceError, Unit>
}