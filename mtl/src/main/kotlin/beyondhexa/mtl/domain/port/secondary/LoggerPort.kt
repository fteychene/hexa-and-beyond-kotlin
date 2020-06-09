package beyondhexa.mtl.domain.port.secondary

import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.LoggerError

interface LoggerPort<F> {

    fun log(message: String): EitherT<F, LoggerError, Unit>
}