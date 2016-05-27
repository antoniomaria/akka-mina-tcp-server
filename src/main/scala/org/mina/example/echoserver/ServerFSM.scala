package org.mina.example.echoserver

import org.apache.mina.core.session.IoSession

import akka.actor.Actor
import org.mina.example.echoserver.Controller.ClientDisconnected

class ServerFSM(val session: IoSession) extends Actor {
  def receive = {
    case "hello" => {
      session.write("hello back to you!")
    }
    case msg: String => session.write(msg)
    case ClientDisconnected() => context.stop(self)
  }

}