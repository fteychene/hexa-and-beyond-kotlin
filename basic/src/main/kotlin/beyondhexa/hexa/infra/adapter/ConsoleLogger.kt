package beyondhexa.hexa.infra.adapter

import arrow.core.Either
import arrow.core.right
import beyondhexa.hexa.domain.model.LoggerError
import beyondhexa.hexa.domain.port.secondary.LoggerPort

class ConsoleLogger: LoggerPort {
    override fun log(message: String): Either<LoggerError, Unit> =
        println(message).right()
}