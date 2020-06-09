package beyondhexa.mtl.domain.port.secondary

import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.Ip
import beyondhexa.mtl.domain.model.SecurityError

interface SecurityAllowerPort<F> {

    fun allowed(ip: Ip): EitherT<F, SecurityError, Boolean>
}