package com.example

import cats._
import cats.std.all._

import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._

case class Config(key: String, secret: String, context: Int)

object Config {
  import cats.data._

  val keyFromConfig: Reader[Config, String] = Reader(_.key)
  val secretFromConfig: Reader[Config, String] = Reader(_.secret)

  val keyAndSecretFromConfig: Reader[Config, (String, String)] =
    for {
      k <- keyFromConfig
      s <- secretFromConfig
    } yield (k, s)
}