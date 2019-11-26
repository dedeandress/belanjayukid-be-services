package utilities

import java.util.UUID

import errors.BadRequest

object Utility {
  def checkUUID(id: String, name: String): UUID ={
    try {
      UUID.fromString(id)
    }catch {
      case _ : IllegalArgumentException => throw BadRequest(s"$name ID is not valid")
    }
  }
}
