/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.httpapi

import java.time.ZonedDateTime

import javax.ws.rs.Path
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.server.{PathMatchers, Route}
import io.cafienne.bounded.aggregate._
import io.cafienne.bounded.cargosample.domain.{CargoCommandValidatorsImpl, CargoDomainProtocol}
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{CargoId, CargoPlanned, TrackingId}
import io.cafienne.bounded.cargosample.projections.CargoQueries
import io.cafienne.bounded.cargosample.projections.QueriesJsonProtocol.CargoViewItem
import io.swagger.annotations._

import scala.util.{Failure, Success}

@Path("/")
@Api(value = "cargo", produces = "application/json", consumes = "application/json")
class CargoRoute(commandGateway: CommandGateway, cargoQueries: CargoQueries)(implicit actorSystem: ActorSystem)
    extends CargoCommandValidatorsImpl(actorSystem)
    with SprayJsonSupport {

  import akka.http.scaladsl.server.Directives._
  import HttpJsonProtocol._
  import io.cafienne.bounded.cargosample.persistence.CargoDomainEventJsonProtocol._

  val logger: LoggingAdapter = Logging(actorSystem, getClass)

  val routes: Route = { getCargo ~ planCargo }

  @Path("cargo/{cargoId}")
  @ApiOperation(
    value = "Fetch the data of a cargo",
    nickname = "getcargo",
    httpMethod = "GET",
    consumes = "application/json",
    produces = "application/json"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "cargoId",
        paramType = "path",
        value = "Unique UUID of the cargo",
        required = true,
        dataType = "string",
        example = "c2ea3e36-2ccd-4a20-9d4f-9495d2a170df"
      ),
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "data of a single cargo",
        responseContainer = "List",
        response = classOf[CargoViewItem]
      ),
      new ApiResponse(code = 204, message = "No content"),
      new ApiResponse(code = 500, message = "Internal server error", response = classOf[ErrorResponse])
    )
  )
  def getCargo =
    get {
      path("cargo" / PathMatchers.JavaUUID) { id =>
        val cargoId = CargoId(id)
        cargoQueries.getCargo(cargoId) match {
          case Some(cargoResponse) => complete(StatusCodes.OK       -> cargoResponse)
          case None                => complete(StatusCodes.NotFound -> ErrorResponse(s"Cargo with id $cargoId is not found"))
        }
      }
    }

  @Path("cargo")
  @ApiOperation(
    value = "Plan a new cargo",
    nickname = "plancargo",
    httpMethod = "POST",
    code = 201,
    consumes = "application/json",
    produces = "application/json"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        required = true,
        paramType = "body",
        dataType = "io.cafienne.bounded.cargosample.httpapi.HttpJsonProtocol$PlanCargo"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 201, message = "data of the newly planned cargo", response = classOf[CargoPlanned]),
      new ApiResponse(code = 203, message = "Processing succeeded but API could not transform the response"),
      new ApiResponse(code = 500, message = "Internal server error", response = classOf[ErrorResponse])
    )
  )
  def planCargo =
    post {
      path("cargo") {
        entity(as[PlanCargo]) { planCargo =>
          val metadata = CommandMetaData(ZonedDateTime.now(), None)
          onComplete(
            commandGateway.sendAndAsk(
              CargoDomainProtocol.PlanCargo(
                metadata,
                CargoId(java.util.UUID.randomUUID()),
                TrackingId(planCargo.trackingId),
                planCargo.routeSpecification
              )
            )
          ) {
            case Success(Ok(List(value: CargoPlanned, _*))) =>
              logger.debug("API received command reply {}", value)
              complete(StatusCodes.Created -> value)
            case Success(Ko(failure)) =>
              logger.warning("API received Ko {} for command: PlanCargo", failure)
              failure match {
                case f => complete(StatusCodes.InternalServerError -> ErrorResponse(f.toString))
              }
            case Success(other) =>
              logger.error("API received Success reply it does not understand: {}", other)
              complete(StatusCodes.NonAuthoritativeInformation -> other.toString)
            case Failure(err) =>
              logger.warning("API received a failed Future with {}", err)
              complete(
                StatusCodes.InternalServerError -> ErrorResponse(
                  err + Option(err.getCause)
                    .map(t => s" due to ${t.getMessage}")
                    .getOrElse("")
                )
              )
          }
        }
      }
    }
}
