package models

import java.util.UUID

case class LoginUser(bearerToken: String, username: String, roleName: String, staffId: UUID)
