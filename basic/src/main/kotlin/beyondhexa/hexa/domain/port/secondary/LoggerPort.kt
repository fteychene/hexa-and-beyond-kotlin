package beyondhexa.hexa.domain.port.secondary

import arrow.core.Either
import beyondhexa.hexa.domain.model.LoggerError

interface LoggerPort {

    fun log(message: String): Either<LoggerError, Unit>
}