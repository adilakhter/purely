package com.example

import scala.annotation.tailrec
import scala.concurrent.Future

import cats._
import cats.std.all._
import cats.syntax.foldable._

import scala.util.Random

import scala.concurrent.ExecutionContext.Implicits.global

object Markov {

  // point out that this has random generation in it, but this is the API we want to use
  def generateString(text: String, context: Int): Future[String] = Future {
    val tokens = text.split(" ").map(_.trim).filterNot(_ == "").toList

    val parts = tokens.sliding(context).toList

    val map: Map[List[String], List[String]] = parts.foldMap { words =>
      Map(words.init -> List(words.last))
    }

    val (startingWords, _) = map.toList(Random.nextInt(map.size))

    @tailrec
    def go(key: List[String], acc: List[String]): List[String] = {

      val nextWordList = map.get(key)

      nextWordList match {
        case Some(list) =>
          val nextWord = list(Random.nextInt(list.size))
          val nextKey = key.drop(1) ++ List(nextWord)
          go(nextKey, nextWord :: acc)
        case None => acc
      }

    }

    val sentence = startingWords ++ go(startingWords, Nil).reverse

    sentence.dropWhile(s => !s.charAt(0).isUpper).mkString(" ").split("(?<=[.?!] )").headOption.getOrElse("").trim.replace("\"", "")
  }
}