package com.example

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import cats.data._
import cats.implicits._

import Twitter._

object Hello extends App {
  import cats.data.Kleisli

  val apiKey = "apiKey"
  val apiSecret = "apiSecret"
  val context = 2

  def getTweets(username: String): ReaderT[Future, Config, List[Tweet]] =  {
    Kleisli.function { c =>
      Twitter.getTweets(username, c.key, c.secret)
    }
  }

  def generateString(text: String): ReaderT[Future, Config, String] = {
    Kleisli.function { c =>
      Markov.generateString(text, c.context)
    }
  }

  def randomString(username: String): ReaderT[Future, Config, String] = {
    for {
      ts <- getTweets(username)
      tweetsAsText = ts.map(_.text).mkString(" ")
      r <- generateString(tweetsAsText)
    } yield r
  }

  def forMe = randomString("mfirry").run(Config("apiKey", "secret", 2))
  Await.result(forMe, 10.second)

}
