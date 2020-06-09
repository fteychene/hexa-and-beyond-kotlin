package beyondhexa.di.infra.io

import arrow.core.Either
import arrow.core.ListK
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.core.k
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.parSequence
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.fix
import arrow.mtl.typeclasses.compose
import arrow.mtl.typeclasses.nest
import arrow.mtl.typeclasses.unnest
import arrow.syntax.function.pipe
import arrow.unsafe
import beyondhexa.di.domain.model.CounterError
import beyondhexa.di.domain.model.Ip
import beyondhexa.di.domain.port.VisitDependencies
import beyondhexa.di.domain.service.visit
import beyondhexa.di.infra.io.adapter.ConsoleLogger
import beyondhexa.di.infra.io.adapter.InMemoryAudience
import beyondhexa.di.infra.io.adapter.InMemorySecurity
import kotlin.random.Random

val ipGenerator: Sequence<Ip> = generateSequence {
    (listOf("192", "168", "10") + Random.nextInt(0, 256).toString())
            .joinToString(".")
}

val printTracker = ListK.functor().compose(IO.functor()).lift { result: Either<CounterError, Int> ->
    result.fold(
            { error -> println("Error on visit application $error") },
            { tracker -> println("Counter $tracker") }
    )
}

fun main() {
    val inMemoryAudience = InMemoryAudience()
    val inMemorySecurity = InMemorySecurity()
    val consoleLogger = ConsoleLogger()

    val di = VisitDependencies(
            async = IO.monadDefer(),
            audienceLoader = inMemoryAudience::load,
            trackerUpdater = inMemoryAudience::save,
            securityIpAllower = inMemorySecurity::allowed,
            logger = consoleLogger::log
    )

    val program =
            ipGenerator.take(1000).toList()
                    .k()
                    .map { visit<ForIO>(it).run(di).fix() }
                    .nest()
                    .pipe(printTracker::invoke)
                    .unnest().fix()
                    .parSequence()


    println("Not executed yet [ENTER to execute]")
    readLine()!!
    unsafe { runBlocking { program } }

    unsafe { runBlocking { inMemoryAudience.load().value() } }.fold(
            { error -> println("Error accessing global counter $error") },
            { tracker -> println("Final counter $tracker") }
    )


}