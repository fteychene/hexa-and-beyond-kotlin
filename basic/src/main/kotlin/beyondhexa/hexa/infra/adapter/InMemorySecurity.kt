package beyondhexa.hexa.infra.adapter

import arrow.core.Either
import arrow.core.right
import beyondhexa.hexa.domain.model.Ip
import beyondhexa.hexa.domain.model.SecurityError
import beyondhexa.hexa.domain.port.secondary.SecurityAllowerPort

class InMemorySecurity : SecurityAllowerPort {

    val denyList = IntRange(0, 30).map { "192.168.10.$it" }.toList()

    override fun allowed(ip: Ip): Either<SecurityError, Boolean> = denyList.contains(ip).not().right()

}