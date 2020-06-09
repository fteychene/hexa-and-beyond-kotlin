package beyondhexa.di.domain.model

sealed class CounterError

data class UnauthorizedIp(val sourceIp: Ip): CounterError()

data class SecurityError(val message: String, val cause: Throwable): CounterError()
data class AudienceError(val message: String, val cause: Throwable): CounterError()
data class LoggerError(val message: String, val cause: Throwable): CounterError()