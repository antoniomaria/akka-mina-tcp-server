package org.mina.example.echoserver

import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.ssl.SslFilter
import scala.collection.mutable.ArrayBuffer
import akka.actor.ActorSystem
import scala.collection.JavaConversions._
import com.google.common.collect.BiMap
import akka.actor.ActorRef
import com.google.common.collect.HashBiMap
import akka.actor.Props
import scala.collection.mutable.Map
import akka.actor.Actor
import scala.collection.concurrent.TrieMap
import RemoteConnection.getAddrString
class ConductorHandler(system: ActorSystem) extends IoHandlerAdapter {

  var clients: Map[IoSession, ActorRef] = TrieMap[IoSession, ActorRef]()

  override def sessionCreated(session: IoSession) {
    session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
  }

  override def sessionOpened(session: IoSession) {
    println("Connection from : " + getAddrString(session));
    val fsm: ActorRef = system.actorOf(Props[ServerFSM])
    clients.put(session, fsm)
  }

  override def sessionClosed(session: IoSession) {
    val fsm: ActorRef = clients.get(session).get
    fsm ! Controller.ClientDisconnected
    clients.remove(session)
  }

  override def sessionIdle(session: IoSession, status: IdleStatus) {
    println("*** IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE) + " ***");
  }

  override def exceptionCaught(session: IoSession, cause: Throwable) {
    clients.remove(session)
    session.closeNow()
  }

  override def messageSent(session: IoSession, message: AnyRef) {

  }

  override def messageReceived(session: IoSession, message: AnyRef) {
    // Write the received data back to remote peer
    println("message from " + getAddrString(session) + " :" + message);
    message match {
      case msg: String => clients(session).tell(msg, Actor.noSender)
      case msg =>
        println("client " + getAddrString(session) + " sent garbage " + msg)
        session.closeOnFlush()
    }
  }

}