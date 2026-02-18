package com.druk.lmplayground.util

import java.net.InetAddress
import java.net.NetworkInterface

object NetworkUtils {
    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is InetAddress) {
                        val ip = address.hostAddress
                        if (ip != null && !ip.contains(":")) { // IPv4
                            return ip
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}
