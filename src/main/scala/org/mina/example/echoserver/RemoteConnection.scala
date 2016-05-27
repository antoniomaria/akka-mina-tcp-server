package org.mina.example.echoserver

import org.apache.mina.core.session.IoSession
import java.net.InetSocketAddress

object RemoteConnection {
  def getAddrString(session: IoSession) = session.getRemoteAddress match {
    case i: InetSocketAddress => i.toString()
    case _ => "[unknown]"
  }
}