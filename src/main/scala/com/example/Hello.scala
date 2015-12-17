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

  def randomString(
      getTweetsF: String => ReaderT[Future, Config, List[Tweet]],
      generateTextF: String => ReaderT[Future, Config, String])
    (username: String): ReaderT[Future, Config, String] = {

    for {
      ts <- getTweetsF(username)
      tweetsAsText = ts.map(_.content).mkString(" ")
      randomString <- generateTextF(tweetsAsText)
    } yield randomString
  }

  randomString(getTweets, generateString)("mfirry")

}
