package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json._

import libs.iteratee.Enumerator
import java.security.MessageDigest

object Application extends Controller {
  def index = Action { request =>
    val res = Map("status" -> "OK", "result" -> "test1")
    //val res = Map("status" -> "OK", "result" -> "test2")
    val digest = MessageDigest.getInstance("MD5").digest(res.toString.getBytes)
    val md5 = digest.map("%02x".format(_)).mkString
    val simple = SimpleResult(
      header = ResponseHeader(200, Map(ETAG -> md5)),
      body = Enumerator(toJson(res))
    )
    request.headers.get("If-None-Match") match {
      case Some(etag) =>
        if (md5 == etag) {
          NotModified
        } else {
          simple
        }
      case None => simple
    }
  }
}
