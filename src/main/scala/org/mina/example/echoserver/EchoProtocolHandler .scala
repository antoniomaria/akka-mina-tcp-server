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

class EchoProtocolHandler(system: ActorSystem) extends IoHandlerAdapter {

  var connections: Map[Long, ActorRef] = Map()

  @throws(classOf[Exception])
  override def sessionCreated(session: IoSession) {
    session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
  }

  @throws(classOf[Exception])
  override def sessionClosed(session: IoSession) {
    connections.remove(session.getId)
  }

  @throws(classOf[Exception])
  override def sessionOpened(session: IoSession) {
    println("OPENED");

    if (!connections.contains(session.getId)) {
      connections(session.getId) = system.actorOf(Props[RequestHandler])
    }
  }

  @throws(classOf[Exception])
  override def sessionIdle(session: IoSession, status: IdleStatus) {
    println("*** IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE) + " ***");
  }

  @throws(classOf[Exception])
  override def exceptionCaught(session: IoSession, cause: Throwable) {
    connections.remove(session.getId)
    session.closeNow()
  }
  @throws(classOf[Exception])
  override def messageSent(session: IoSession, message: AnyRef) {
   
  }

  @throws(classOf[Exception])
  override def messageReceived(session: IoSession, message: AnyRef) {
    // Write the received data back to remote peer
    println("Received : " + message);
    session.write("Echo: " + message);
    val requestHandler: ActorRef = connections(session.getId())
    
    requestHandler.tell(message, Actor.noSender)
  }

}