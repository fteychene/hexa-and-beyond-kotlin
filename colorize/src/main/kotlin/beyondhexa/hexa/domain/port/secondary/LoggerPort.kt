package beyondhexa.hexa.domain.port.secondary

import arrow.core.Either
import arrow.fx.IO
import beyondhexa.hexa.domain.model.LoggerError

interface LoggerPort {

    fun log(message: String): IO<Either<LoggerError, Unit>>
}