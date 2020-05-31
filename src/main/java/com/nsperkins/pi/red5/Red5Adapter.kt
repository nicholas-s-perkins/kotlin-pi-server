package com.nsperkins.pi.red5

import org.red5.server.adapter.MultiThreadedApplicationAdapter
import org.red5.server.api.IClient
import org.red5.server.api.IConnection
import org.red5.server.api.Red5
import org.red5.server.api.stream.IBroadcastStream
import java.util.*
import javax.management.Attribute
import javax.management.AttributeList
import javax.management.DynamicMBean
import javax.management.MBeanInfo

open class Red5Adapter : MultiThreadedApplicationAdapter(), DynamicMBean{
    override fun streamBroadcastStart(stream: IBroadcastStream?) {
        val connection = Red5.getConnectionLocal()
        if (connection != null && stream != null) {
            println("Broadcast started for: " + stream.publishedName)
            connection.setAttribute("streamStart", System.currentTimeMillis())
            connection.setAttribute("streamName", stream.publishedName)
        }
    }

    fun getLiveStreams(): List<String> {
        val iter: Iterator<IClient> = scope.clients.iterator()
        val streams: MutableList<String> = ArrayList()
        THE_OUTER@ while (iter.hasNext()) {
            val client = iter.next()
            val cset: Iterator<IConnection> = client.connections.iterator()
            THE_INNER@ while (cset.hasNext()) {
                val c = cset.next()
                if (c.hasAttribute("streamName")) {
                    if (!c.isConnected) {
                        try {
                            c.close()
                            client.disconnect()
                        } catch (e: Exception) {
                            // Failure to close/disconnect.
                        }
                        continue@THE_OUTER
                    }
                    if (streams.contains(c.getAttribute("streamName").toString())) {
                        continue@THE_INNER
                    }
                    streams.add(c.getAttribute("streamName").toString())
                }
            }
        }
        return streams
    }

    override fun getMBeanInfo(): MBeanInfo? {
        return MBeanInfo(javaClass.name, null, null, null, null, null);
    }

    override fun setAttribute(attribute: Attribute?) {
        TODO("Not yet implemented")
    }

    override fun setAttributes(attributes: AttributeList?): AttributeList {
        TODO("Not yet implemented")
    }

    override fun getAttributes(attributes: Array<out String>?): AttributeList {
        TODO("Not yet implemented")
    }

    override fun invoke(actionName: String?, params: Array<out Any>?, signature: Array<out String>?): Any {
        TODO("Not yet implemented")
    }
}
