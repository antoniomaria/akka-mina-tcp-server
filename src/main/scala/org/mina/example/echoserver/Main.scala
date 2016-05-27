package org.mina.example.echoserver

import org.apache.mina.transport.socket.SocketAcceptor
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder
import java.net.InetSocketAddress
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.TextLineEncoder
import org.apache.mina.filter.codec.textline.TextLineDecoder
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import java.nio.charset.Charset
import akka.actor.ActorSystem

object Main extends App {

  val PORT = 23

  val system = ActorSystem("System")
  
  val acceptor: SocketAcceptor = new NioSocketAcceptor();
  acceptor.setReuseAddress(true);

  val pcf = new ProtocolCodecFilter(new TextLineCodecFactory())

  val chain: DefaultIoFilterChainBuilder = acceptor.getFilterChain();
  chain.addLast("codec", pcf)
  
  acceptor.setHandler(new ConductorHandler(system));

  acceptor.bind(new InetSocketAddress(PORT));

  System.out.println("Listening on port " + PORT);

  while (true) {
    System.out.println("R: " + acceptor.getStatistics().getReadBytesThroughput() +
      ", W: " + acceptor.getStatistics().getWrittenBytesThroughput());
    Thread.sleep(9000);
  }
}

