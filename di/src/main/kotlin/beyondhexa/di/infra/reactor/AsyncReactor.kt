package beyondhexa.di.infra.reactor

import arrow.core.k
import arrow.fx.reactor.*
import arrow.fx.reactor.extensions.fluxk.monadDefer.monadDefer
import arrow.fx.reactor.extensions.monok.monadDefer.monadDefer
import beyondhexa.di.domain.port.VisitDependencies
import beyondhexa.di.domain.model.Ip
import beyondhexa.di.domain.service.visit
import beyondhexa.di.infra.reactor.adapter.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.random.Random

fun ipGenerator(): Sequence<Ip> = generateSequence {
    (listOf("192", "168", "10") + Random.nextInt(0, 256).toString())
            .joinToString(".")
}

fun fluxRun() {
    val audience = FluxInMemoryAudience()
    val security = FluxInMemorySecurity()
    val logger = FluxConsoleLogger()
    val visitDependencies = VisitDependencies(
            async = FluxK.monadDefer(),
            audienceLoader = audience::load,
            trackerUpdater = audience::save,
            securityIpAllower = security::allowed,
            logger = logger::log
    )
    Flux.merge(ipGenerator().take(2000).toList()
            .k()
            .map{ visit<ForFluxK>(it).run(visitDependencies) }
            .map { it.value() })
            .subscribe { result ->
                result.fold(
                        { error -> println("Error on visit application $error") },
                        { tracker -> println("Counter $tracker") }
                )
            }

    println("Press enter to continue")
    readLine()!!

    visitDependencies.audienceLoader().value().fix().flux.subscribe {
        it.fold(
                { error -> println("Error accessing global counter $error") },
                { tracker -> println("Final counter $tracker") }
        )
    }
}

fun monoRun() {
    val audience = MonoInMemoryAudience()
    val security = MonoInMemorySecurity()
    val logger = MonoConsoleLogger()
    val visitDependencies = VisitDependencies(
            async = MonoK.monadDefer(),
            audienceLoader = audience::load,
            trackerUpdater = audience::save,
            securityIpAllower = security::allowed,
            logger = logger::log
    )
    Flux.merge(ipGenerator().take(2000).toList()
            .k()
            .map { visit<ForMonoK>(it).run(visitDependencies) }
            .map { it.value() })
            .subscribe { result ->
                result.fold(
                        { error -> println("Error on visit application $error") },
                        { tracker -> println("Counter $tracker") }
                )
            }

    println("Press enter to continue")
    readLine()

    visitDependencies.audienceLoader().value().fix().mono.subscribe {
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