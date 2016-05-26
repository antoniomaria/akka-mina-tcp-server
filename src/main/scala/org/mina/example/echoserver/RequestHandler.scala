package org.mina.example.echoserver

import akka.actor.Actor

class RequestHandler extends Actor{
  def receive = {
    case "hello" => println ("hello back to you!")
    case msg => println ("huuh?" + msg) 
  }
  
}