package beyondhexa.mtl.infra.rx

import arrow.core.k
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.extensions.singlek.monadDefer.monadDefer
import arrow.fx.rx2.fix
import beyondhexa.mtl.domain.model.Ip
import beyondhexa.mtl.domain.service.Counter
import beyondhexa.mtl.infra.rx.adapter.ConsoleLogger
import beyondhexa.mtl.infra.rx.adapter.InMemoryAudience
import beyondhexa.mtl.infra.rx.adapter.InMemorySecurity
import io.reactivex.Observable
import kotlin.random.Random

val ipGenerator: Sequence<Ip> = generateSequence {
    (listOf("192", "168", "10") + Random.nextInt(0, 256).toString())
            .joinToString(".")
}


fun main() {
    val counter = Counter(
            async = SingleK.monadDefer(),
            audience = InMemoryAudience(),
            security = InMemorySecurity(),
            logger = ConsoleLogger()
    )

    Observable.merge(ipGenerator.take(2000).toList()
            .k()
            .map(counter::visit)
            .map { it.value().fix().single.toObservable() })
            .subscribe { result ->
                result.fold(
                        { error -> println("Error on visit application $error") },
                        { tracker -> println("Counter $tracker") }
                )
            }

    readLine()

    counter.audience.load().value().fix().single.subscribe { result ->
        result.fold(
                { error -> println("Error accessing global counter $error") },
                { tracker -> println("Final counter $tracker") }
        )
    }
}