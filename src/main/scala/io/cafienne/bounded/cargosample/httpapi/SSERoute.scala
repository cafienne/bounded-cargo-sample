/*
 * Copyright (C) 2018-2021  Cafienne B.V.
 */

package io.cafienne.bounded.cargosample.httpapi

import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{PathMatchers, Route}
import io.cafienne.bounded.aggregate._
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{
  CargoCommandMetaData,
  CargoId,
  CargoPlanned,
  TrackingId
}
import io.cafienne.bounded.cargosample.domain.{CargoCommandValidatorsImpl, CargoDomainProtocol}
import io.cafienne.bounded.cargosample.eventmaterializers.CargoQueries
import io.cafienne.bounded.cargosample.eventmaterializers.QueriesJsonProtocol.CargoViewItem
import javax.ws.rs.Path
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.NotUsed
import akka.stream.scaladsl.Source

import scala.concurrent.duration._
import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME

@Path("/")
class SSERoute(commandGateway: CommandGateway, cargoQueries: CargoQueries)(implicit actorSystem: ActorSystem)
    extends CargoCommandValidatorsImpl(actorSystem)
    with SprayJsonSupport {

  import HttpJsonProtocol._
  import akka.http.scaladsl.server.Directives._
  import io.cafienne.bounded.cargosample.persistence.CargoDomainEventJsonProtocol._

  val logger: LoggingAdapter = Logging(actorSystem, getClass)

  val routes: Route = { streamEvents }

  def streamEvents: server.Route = {
    import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
    get {
      path("events") {
        complete {
          Source
            .tick(2.seconds, 2.seconds, NotUsed)
            .map(_ => LocalTime.now())
            .map(time => ServerSentEvent(ISO_LOCAL_TIME.format(time)))
            .keepAlive(1.second, () => ServerSentEvent.heartbeat)
        }
      }
    }
  }
}
