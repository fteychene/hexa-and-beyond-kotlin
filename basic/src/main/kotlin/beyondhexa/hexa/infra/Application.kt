package beyondhexa.hexa.infra

import arrow.core.Either
import arrow.core.ListK
import arrow.core.extensions.listk.functor.functor
import arrow.core.k
import arrow.syntax.function.pipe
import beyondhexa.hexa.domain.model.CounterError
import beyondhexa.hexa.domain.model.Ip
import beyondhexa.hexa.domain.service.Counter
import beyondhexa.hexa.infra.adapter.ConsoleLogger
import beyondhexa.hexa.infra.adapter.InMemoryAudience
import beyondhexa.hexa.infra.adapter.InMemorySecurity
import kotlin.random.Random

val ipGenerator: Sequence<Ip> = generateSequence {
    (listOf("192", "168", "10") + Random.nextInt(0, 256).toString())
            .joinToString(".")
}

val printTracker = ListK.functor().lift { result: Either<CounterError, Int> ->
    result.fold(
            { error -> println("Error on visit application $error") },
            { tracker -> println("Counter $tracker") }
    )
}

fun main() {
    val counter = Counter(
            audience = InMemoryAudience(),
            security = InMemorySecurity(),
            logger = ConsoleLogger()
    )

    ipGenerator.take(2000).toList().k()
            .map(counter::visit)
            .pipe(printTracker)

}