package beyondhexa.mtl.domain.model

import arrow.optics.optics

@optics
data class Tracker(
        val counter: Int = 0,
        val ips: List<Ip> = emptyList()
) {
    companion object
}