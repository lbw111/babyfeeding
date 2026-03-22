package com.babyfeeding.discovery

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LanDiscovery {
    companion object {
        private const val DISCOVERY_PORT = 8766
        private const val DISCOVER_MESSAGE = "BFT_DISCOVER"
        private const val RESPONSE_PREFIX = "BFT_HERE:"
        private const val BUFFER_SIZE = 1024
        private const val DEFAULT_TIMEOUT_MS = 1_500
    }

    suspend fun discoverBaseUrl(timeoutMs: Int = DEFAULT_TIMEOUT_MS): String? = withContext(Dispatchers.IO) {
        runCatching {
            DatagramSocket().use { socket ->
                socket.broadcast = true
                socket.soTimeout = timeoutMs

                val message = DISCOVER_MESSAGE.toByteArray(Charsets.UTF_8)
                val packet = DatagramPacket(
                    message,
                    message.size,
                    InetAddress.getByName("255.255.255.255"),
                    DISCOVERY_PORT
                )
                socket.send(packet)

                val buffer = ByteArray(BUFFER_SIZE)
                val response = DatagramPacket(buffer, buffer.size)
                socket.receive(response)

                val payload = String(
                    response.data,
                    0,
                    response.length,
                    Charsets.UTF_8
                ).trim()

                parseBaseUrl(payload)
            }
        }.getOrNull()
    }

    private fun parseBaseUrl(payload: String): String? {
        if (!payload.startsWith(RESPONSE_PREFIX)) {
            return null
        }

        val hostAndPort = payload.removePrefix(RESPONSE_PREFIX)
        val separatorIndex = hostAndPort.lastIndexOf(':')
        if (separatorIndex <= 0 || separatorIndex == hostAndPort.lastIndex) {
            return null
        }

        val host = hostAndPort.substring(0, separatorIndex)
        val port = hostAndPort.substring(separatorIndex + 1)
        return "http://$host:$port/"
    }
}
