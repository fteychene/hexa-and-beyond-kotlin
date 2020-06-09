package beyondhexa.hexa.domain.port.primary

import arrow.core.Either
import beyondhexa.hexa.domain.model.CounterError
import beyondhexa.hexa.domain.model.Ip

interface CounterPort {
    fun visit(sourceIp: Ip): Either<CounterError, Int>
}