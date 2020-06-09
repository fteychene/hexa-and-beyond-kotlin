package beyondhexa.hexa.domain.port.secondary

import arrow.core.Either
import arrow.fx.IO
import beyondhexa.hexa.domain.model.Ip
import beyondhexa.hexa.domain.model.SecurityError

interface SecurityAllowerPort {

    fun allowed(ip: Ip): IO<Either<SecurityError, Boolean>>
}