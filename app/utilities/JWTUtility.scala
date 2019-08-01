package utilities

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import errors.AuthorizationException
import graphql.Context
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

  private def isValidJWT(jwt: String): Boolean ={
    Jwt.isValid(jwt, BelanjaYukConstant.secretKey, Seq(JwtAlgorithm.HS256))
  }

  private def extractToken(authHeader: String): Option[String] = {
    authHeader.split("Bearer ") match {
      case Array(_, token) => Some(token)
      case _ => None
    }
  }

  def getId(jwt: String): String = {
    val json = Jwt.decodeRaw(jwt, BelanjaYukConstant.secretKey, Seq(JwtAlgorithm.HS256))
    Json.parse(json.get).get("id").asText()
  }

  private def getRole(jwt: String): String = {
    val json = Jwt.decodeRaw(jwt, BelanjaYukConstant.secretKey, Seq(JwtAlgorithm.HS256))
    val role = Json.parse(json.get).get("role").asText()
    play.Logger.warn(role)
    role
  }

  private def getToken(context: Context): String = {
    if(context.requestHeaders.get("Authorization").nonEmpty) {
      val authHeader = context.requestHeaders.get("Authorization").get
      if (extractToken(authHeader).nonEmpty) {
        val token = extractToken(authHeader).get
        if(isValidJWT(token)) {
          play.Logger.warn(token)
          return token
        }
        throw AuthorizationException("token was expired")
      }
      throw AuthorizationException("You are not authorized")
    }
    throw AuthorizationException("You are not authorized")
  }

  def isAdminOrCashier(context: Context): Boolean = {
    if(JWTUtility.getRole(JWTUtility.getToken(context)).equals("Admin") ||
      JWTUtility.getRole(JWTUtility.getToken(context)).equals("Cashier")) return true
    false
  }

  def isAdmin(context: Context): Boolean = {
    if(JWTUtility.getRole(JWTUtility.getToken(context)).equals("Admin")) return true
    false
  }

}
