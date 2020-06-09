package beyondhexa.mtl.domain.port.primary

import arrow.mtl.EitherT
import beyondhexa.mtl.domain.model.CounterError
import beyondhexa.mtl.domain.model.Ip

interface CounterPort<F> {
    fun visit(sourceIp: Ip): EitherT<F, CounterError, Int>
}