package com.example

import io.circe._
import io.circe.parse._
import io.circe.syntax._
import io.circe.Decoder._

import cats._
import cats.std.all._
import cats.data.Xor
import cats.syntax.eq._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Twitter {
  import io.circe.generic.auto._

  case class UserDetails(screen_name: String)
  case class Tweet(user: UserDetails, text: String)

  def getTweets(twitterHandle: String, apiKey: String, apiSecret: String): Future[List[Tweet]] = Future {
    val source = scala.io.Source.fromFile(s"${twitterHandle.toLowerCase}.json").mkString
    val tweets = decode[List[Tweet]](source)

    tweets match {
      case Xor.Right(ts) =>
        ts.filter {
          case Tweet(UserDetails(screenName), content) => {
            screenName.toLowerCase === twitterHandle.toLowerCase && !(content.startsWith("RT") || content.startsWith("@"))
          }
        }
      case Xor.Left(_) => throw new RuntimeException("Cannot parse response")
    }
  }

}