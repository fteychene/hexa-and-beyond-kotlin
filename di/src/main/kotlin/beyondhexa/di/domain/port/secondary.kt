package beyondhexa.di.domain.port

import arrow.mtl.EitherT
import beyondhexa.di.domain.model.*

typealias AudienceLoader<F> = () -> EitherT<F, AudienceError, Tracker>

typealias TrackerUpdate<F> = (Tracker) -> EitherT<F, AudienceError, Unit>

typealias SecurityIpAllower<F> = (Ip) -> EitherT<F, SecurityError, Boolean>

typealias Logger<F> = (String) -> EitherT<F, LoggerError, Unit>
