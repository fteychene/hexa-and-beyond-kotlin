package beyondhexa.di.infra.rx

import arrow.core.k
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.extensions.singlek.monadDefer.monadDefer
import arrow.fx.rx2.fix
import arrow.fx.rx2.value
import beyondhexa.di.domain.model.Ip
import beyondhexa.di.domain.port.VisitDependencies
import beyondhexa.di.domain.service.visit
import beyondhexa.di.infra.rx.adapter.ConsoleLogger
import beyondhexa.di.infra.rx.adapter.InMemoryAudience
import beyondhexa.di.infra.rx.adapter.InMemorySecurity
import io.reactivex.Observable
import kotlin.random.Random

val ipGenerator: Sequence<Ip> = generateSequence {
    (listOf("192", "168", "10") + Random.nextInt(0, 256).toString())
            .joinToString(".")
}


fun main() {
    val audience = InMemoryAudience()
    val security = InMemorySecurity()
    val logger = ConsoleLogger()
    val visitDependencies = VisitDependencies(
            async = SingleK.monadDefer(),
            audienceLoader = audience::load,
            trackerUpdater = audience::save,
            securityIpAllower = security::allowed,
            logger = logger::log
    )

    Observable.merge(ipGenerator.take(2000).toList()
            .k()
            .map { visit<ForSingleK>(it).run(visitDependencies).fix() }
            .map { it.value().toObservable() })
            .subscribe { result ->
                result.fold(
                        { error -> println("Error on visit application $error") },
                        { tracker -> println("Counter $tracker") }
                )
            }

    println("Press enter to continue")
    readLine()

    audience.load().value().fix().single.subscribe { result ->
        result.fold(
                { error -> println("Error accessing global counter $error") },
                { tracker -> println("Final counter $tracker") }
        )
    }
}