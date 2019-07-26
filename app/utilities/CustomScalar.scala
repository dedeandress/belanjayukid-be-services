package utilities

import akka.http.scaladsl.model.DateTime
import sangria.ast.StringValue
import sangria.validation.{BaseViolation, FloatCoercionViolation, Violation}
import java.util.UUID

import sangria.ast
import sangria.marshalling.MarshallerCapability
import sangria.schema.ScalarType

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

  def valueOutput[T](value: T, capabilities: Set[MarshallerCapability]): T = value

  implicit val BigDecimalType = ScalarType[BigDecimal]("BigDecimal",
    description = Some("The `BigDecimal` scalar type represents signed fractional values with arbitrary precision."),
    coerceOutput = valueOutput,
    coerceUserInput = {
      case i: Int ⇒ Right(BigDecimal(i))
      case i: Long ⇒ Right(BigDecimal(i))
      case i: BigInt ⇒ Right(BigDecimal(i))
      case d: Double ⇒ Right(BigDecimal(d))
      case d: BigDecimal ⇒ Right(d)
      case _ ⇒ Left(FloatCoercionViolation)
    },
    coerceInput = {
      case ast.BigDecimalValue(d, _, _) ⇒ Right(d)
      case ast.FloatValue(d, _, _) ⇒ Right(BigDecimal(d))
      case ast.IntValue(i, _, _) ⇒ Right(BigDecimal(i))
      case ast.BigIntValue(i, _, _) ⇒ Right(BigDecimal(i))
      case _ ⇒ Left(FloatCoercionViolation)
    })

}
