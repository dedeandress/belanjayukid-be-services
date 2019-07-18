package utilities

import akka.http.scaladsl.model.DateTime
import sangria.ast.StringValue
import sangria.validation.Violation
import java.util.UUID
import sangria.ast
import sangria.schema.ScalarType
import sangria.validation.BaseViolation
import scala.util.{Failure, Success, Try}

object CustomScalar {
  
  case object UUIDViolation extends BaseViolation("Invalid UUID")

  def parseUuid(s: String) = Try(UUID.fromString(s)) match {
    case Success(s) ⇒ Right(s)
    case Failure(e) ⇒ Left(UUIDViolation)
  }
  
  implicit val UUIDType =
    ScalarType[UUID]("UUID",
      coerceOutput = (v, _) ⇒ v.toString,
      coerceUserInput = {
        case s: String ⇒ parseUuid(s)
        case _ ⇒ Left(UUIDViolation)
      },
      coerceInput = {
        case ast.StringValue(s, _, _, _, _) ⇒ parseUuid(s)
        case _ ⇒ Left(UUIDViolation)
      }
    )

  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error during parsing DateTime"
  }

  implicit val GraphQLDateTime = ScalarType[DateTime](//1
    "DateTime",
    coerceOutput = (dt, _) => dt.toString,
    coerceInput = {
      case StringValue(dt, _, _ , _, _) => DateTime.fromIsoDateTimeString(dt).toRight(DateTimeCoerceViolation)
      case _ => Left(DateTimeCoerceViolation)
    },
    coerceUserInput = {
      case s: String => DateTime.fromIsoDateTimeString(s).toRight(DateTimeCoerceViolation)
      case _ => Left(DateTimeCoerceViolation)
    }
  )

}
