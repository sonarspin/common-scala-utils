package com.sonarspin.scala.lang

import play.api.libs.json._

/**
 * Trait to that defines JSON (de)serialization to sealed traits / case objects.
 *
 * ==Example Usage==
 * {{{
 *   sealed trait EventType extends EventType.Value
 *
 *   object EventType extends Enum[EventType] {
 *     val values = List(CREATE, UPDATE)
 *
 *     case object CREATE extends EventType
 *
 *     case object UPDATE extends EventType
 *   }
 * }}}
 *
 * @tparam A The Enum type
 */
trait Enum[A] {

  trait Value {
    self: A =>
  }

  val values: Seq[A]

  def parse(v: String): Option[A] = values.find(_.toString == v)

  def error(enumName: String): JsError = JsError(s"String value '$enumName' is not a valid enum item ")

  implicit def reads: Reads[A] = {
    case JsString(v) => parse(v) match {
      case Some(a) => JsSuccess(a)
      case _ => error(v)
    }
    case _ => JsError("String value expected")
  }

  implicit def writes: Writes[A] = (v: A) => JsString(v.toString)

  implicit def format: Format[A] = Format(reads, writes)
}