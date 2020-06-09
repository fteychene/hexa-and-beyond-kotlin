package beyondhexa.mtl.infra.io

import arrow.fx.extensions.io.concurrent.parSequence
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.unsafe
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

fun main() {
    val counter = Counter(
            audience = InMemoryAudience(),
            security = InMemorySecurity(),
            logger = ConsoleLogger()
    )

    val program =
            ipGenerator.take(1000).toList()
                    .map(counter::visit)
                    .map {
                        it.map { result ->
                            result.fold(
                                    { error -> println("Error on visit application $error") },
                                    { tracker -> println("Counter $tracker") }
                            )
                        }
                    }
                    .parSequence()


    println("Not executed yet [ENTER to execute]")
    readLine()!!
    unsafe { runBlocking { program } }

    unsafe { runBlocking { counter.audience.load() } }.fold(
            { error -> println("Error accessing global counter $error") },
            { tracker -> println("Final counter $tracker") }
    )


}