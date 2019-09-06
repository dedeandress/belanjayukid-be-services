package controllers

import play.api.mvc._
import com.google.inject.{Inject, Singleton}
import graphql.{Context, GraphQL}
import sangria.marshalling.playJson._
import play.api.libs.json._
import sangria.ast.Document
import sangria.parser.QueryParser
import sangria.execution._
import errors.{AuthenticationException, AuthorizationException, TooComplexQueryError}
import sangria.execution.Executor
import sangria.execution.QueryAnalysisError

import scala.util.{Success, Try}
import scala.util.Failure
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

@Singleton
class AppController @Inject()(graphQL: GraphQL, cc: ControllerComponents, implicit val executionContext: ExecutionContext) extends AbstractController(cc){

  def graphiql = Action(Ok(views.html.graphiql()))

  def graphqlBody: Action[JsValue] = Action.async(parse.json) {
    implicit request: Request[JsValue] =>

      val extract: JsValue => (String, Option[String], Option[JsObject]) = query => (
        (query \ "query").as[String],
        (query \ "operationName").asOpt[String],
        (query \ "variables").toOption.flatMap {
          case JsString(vars) => Some(parseVariables(vars))
          case obj: JsObject => Some(obj)
          case _ => None
        }
      )

      val maybeQuery: Try[(String, Option[String], Option[JsObject])] = Try {
        request.body match {
          case arrayBody@JsArray(_) => extract(arrayBody.value(0))
          case objectBody@JsObject(_) => extract(objectBody)
          case otherType =>
            throw new Error {

            }
        }
      }

      maybeQuery match {
        case Success((query, operationName, variables)) =>
          val httpContext = Context(request.headers, request.cookies)
          executeQuery(query, variables, operationName, httpContext)
            .map(_.withHeaders(httpContext.newHeaders: _*).withCookies(httpContext.newCookies: _*))
        case Failure(error) => Future.successful {
          BadRequest(error.getMessage)
        }
      }
  }

  def executeQuery(query: String, variables: Option[JsObject] = None, operation: Option[String] = None, context: Context): Future[Result] = QueryParser.parse(query) match {
    case Success(queryAst: Document) => Executor.execute(
        schema = graphQL.Schema,
        userContext = context,
        queryAst = queryAst,
        variables = variables.getOrElse(Json.obj()),
        queryReducers = List(
          QueryReducer.rejectMaxDepth[Context](graphQL.maxQueryDepth),
          QueryReducer.rejectComplexQueries[Context](graphQL.maxQueryComplexity, (_, _) => TooComplexQueryError())
        )
      ).map(Ok(_)).recover {
        case error: QueryAnalysisError => BadRequest(error.resolveError)
        case error: ErrorWithResolver => InternalServerError(error.resolveError)
      }
    case Failure(ex) => Future(BadRequest(s"${ex.getMessage}"))
  }

  def parseVariables(variables: String): JsObject = if (variables.trim.isEmpty || variables.trim == "null") Json.obj()
  else Json.parse(variables).as[JsObject]

  val ErrorHandler = ExceptionHandler {
    case (_, AuthenticationException(message)) ⇒ HandledException(message)
    case (_, AuthorizationException(message)) ⇒ HandledException(message)
  }

}
