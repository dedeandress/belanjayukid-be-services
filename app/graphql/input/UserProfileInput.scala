package graphql.input

import java.util.UUID

import akka.http.scaladsl.model.DateTime

case class UserProfileInput(fullName: String, phoneNumber: String, address: String, noNik: String, dateOfBirth: Long)
