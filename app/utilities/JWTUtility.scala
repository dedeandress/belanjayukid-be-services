package utilities

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import models.User
import pdi.jwt.{Jwt, JwtAlgorithm}
import play.libs.Json

object JWTUtility {

  def generateJWT(user: User, roleName: String): String = {
    val algorithm: Algorithm = Algorithm.HMAC256(BelanjaYukConstant.secretKey)
    JWT.create().withIssuer("BelanjaYuk.id")
      .withClaim("id", user.id.toString).withClaim("role", roleName)
      .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(60).toInstant))
      .sign(algorithm)
  }

  def isValidJWT(jwt: String): Boolean ={
    Jwt.isValid(jwt, BelanjaYukConstant.secretKey, Seq(JwtAlgorithm.HS256))
  }

  def extractToken(authHeader: String): Option[String] = {
    authHeader.split("Bearer ") match {
      case Array(_, token) => Some(token)
      case _ => None
    }
  }

  def getId(jwt: String): String = {
    val json = Jwt.decodeRaw(jwt, BelanjaYukConstant.secretKey, Seq(JwtAlgorithm.HS256))
    Json.parse(json.get).get("id").asText()
  }

  def getRole(jwt: String): String = {
    val json = Jwt.decodeRaw(jwt, BelanjaYukConstant.secretKey, Seq(JwtAlgorithm.HS256))
    Json.parse(json.get).get("role").asText()
  }
}
