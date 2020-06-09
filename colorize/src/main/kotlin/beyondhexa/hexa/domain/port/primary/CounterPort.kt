package beyondhexa.hexa.domain.port.primary

import arrow.core.Either
import arrow.fx.IO
import beyondhexa.hexa.domain.model.CounterError
import beyondhexa.hexa.domain.model.Ip

interface CounterPort {
    fun visit(sourceIp: Ip): IO<Either<CounterError, Int>>
}