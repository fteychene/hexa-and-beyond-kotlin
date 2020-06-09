package beyondhexa.hexa.infra.adapter

import arrow.core.Either
import arrow.core.right
import beyondhexa.hexa.domain.model.AudienceError
import beyondhexa.hexa.domain.model.Tracker
import beyondhexa.hexa.domain.port.secondary.AudiencePort

class InMemoryAudience: AudiencePort {

    var audience: Tracker = Tracker()

    override fun load(): Either<AudienceError, Tracker> = audience.right()

    override fun save(tracker: Tracker): Either<AudienceError, Unit> {
        audience = tracker
        return Unit.right()
    }

}