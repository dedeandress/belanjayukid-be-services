package utilities

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.inject.Inject
import models.User
import play.api.mvc.Headers
import com.auth0.jwt.interfaces.DecodedJWT
import repositories.repositoryInterfaces.UserRepository


class JWTUtility @Inject()(userRepository: UserRepository){

  def getUser(headers: Headers) = {
    import errors.Unauthorized
    if(headers.get("Authorization").isDefined) {
      import java.util.UUID
      val token = JWTUtility.extractToken(headers.get("Authorization").get)
      val decodeJWT = JWTUtility.validateJWT(token.get)
      val id = decodeJWT.getClaim("id").asString()
      userRepository.find(UUID.fromString(id))
    }else throw new Unauthorized("Unauthorized")
  }
}

object JWTUtility {

  def generateJWT(user: User) = {
    val algorithm: Algorithm = Algorithm.HMAC256(BelanjaYukConstant.secretKey)
    JWT.create().withIssuer("BelanjaYuk.id")
      .withClaim("id", user.id.toString)
      .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(60).toInstant))
      .sign(algorithm)
  }

  def validateJWT(jwt: String): DecodedJWT = {
    val algorithm: Algorithm = Algorithm.HMAC256(BelanjaYukConstant.secretKey)
    JWT.require(algorithm).withIssuer("BelanjaYuk.id")
      .build().verify(jwt)
  }

  def extractToken(authHeader: String): Option[String] = {
    authHeader.split("Bearer ") match {
      case Array(_, token) => Some(token)
      case _ => None
    }
  }
}
