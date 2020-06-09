package beyondhexa.mtl.infra.reactor

import arrow.core.k
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.extensions.fluxk.monadDefer.monadDefer
import arrow.fx.reactor.extensions.monok.monadDefer.monadDefer
import arrow.fx.reactor.fix
import beyondhexa.mtl.domain.model.Ip
import beyondhexa.mtl.domain.service.Counter
import beyondhexa.mtl.infra.reactor.adapter.*
import reactor.core.publisher.Flux
import kotlin.random.Random

fun ipGenerator(): Sequence<Ip> = generateSequence {
    (listOf("192", "168", "10") + Random.nextInt(0, 256).toString())
            .joinToString(".")
}

fun fluxRun() {
    val counter = Counter(
            async = FluxK.monadDefer(),
            audience = FluxInMemoryAudience(),
            security = FluxInMemorySecurity(),
            logger = FluxConsoleLogger()
    )
    Flux.merge(ipGenerator().take(2000).toList()
            .k()
            .map(counter::visit)
            .map { it.value().fix().flux })
            .subscribe { result ->
                result.fold(
                        { error -> println("Error on visit application $error") },
                        { tracker -> println("Counter $tracker") }
                )
            }

    readLine()

    counter.audience.load().value().fix().flux.subscribe {
        it.fold(
                { error -> println("Error accessing global counter $error") },
                { tracker -> println("Final counter $tracker") }
        )
    }
}

fun monoRun() {
    val counter = Counter(
            async = MonoK.monadDefer(),
            audience = MonoInMemoryAudience(),
            security = MonoInMemorySecurity(),
            logger = MonoConsoleLogger()
    )
    Flux.merge(ipGenerator().take(2000).toList()
            .k()
            .map(counter::visit)
            .map { it.value().fix().mono })
            .subscribe { result ->
                result.fold(
                        { error -> println("Error on visit application $error") },
                        { tracker -> println("Counter $tracker") }
                )
            }

    readLine()

    counter.audience.load().value().fix().mono.subscribe {
        it.fold(
                { error -> println("Error accessing global counter $error") },
                { tracker -> println("Final counter $tracker") }
        )
    }
}

fun main() {
    println("Run with Flux")
    fluxRun()
    println("Run with Mono")
    monoRun()
}