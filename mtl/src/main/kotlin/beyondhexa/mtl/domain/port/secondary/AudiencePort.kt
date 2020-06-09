package beyondhexa.mtl.domain.port.secondary

import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.AudienceError
import beyondhexa.mtl.domain.model.Tracker

interface AudiencePort<F> {

    fun load(): EitherT<F, AudienceError, Tracker>

    fun save(tracker: Tracker): EitherT<F, AudienceError, Unit>
}