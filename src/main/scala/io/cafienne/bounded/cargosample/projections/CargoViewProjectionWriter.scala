/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.projections

import akka.Done
import akka.actor.ActorSystem
import io.cafienne.bounded.eventmaterializers.AbstractReplayableEventMaterializer
import com.typesafe.scalalogging.Logger
import io.cafienne.bounded.cargosample.domain.Cargo
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{CargoPlanned, NewRouteSpecified}
import io.cafienne.bounded.cargosample.projections.QueriesJsonProtocol.CargoViewItem
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class CargoViewProjectionWriter(actorSystem: ActorSystem, lmdbClient: LmdbClient)
    extends AbstractReplayableEventMaterializer(actorSystem) {

  /**
    * Tagname used to identify eventstream to listen to
    */
  override val tagName: String = Cargo.aggregateRootTag

  /**
    * Mapping name of this listener
    */
  override val matMappingName: String = "cargo-view"

  override lazy val logger: Logger = Logger(LoggerFactory.getLogger(CargoViewProjectionWriter.this.getClass))

  override def handleReplayEvent(evt: Any): Future[Done] = handleEvent(evt)

  override def handleEvent(evt: Any): Future[Done] = {
    try {
      evt match {
        case event: CargoPlanned =>
          val cargoViewItem = CargoViewItem(
            event.cargoId,
            event.routeSpecification.origin.name,
            event.routeSpecification.destination.name,
            event.routeSpecification.arrivalDeadline
          )
          lmdbClient.put(event.cargoId.idAsString, cargoViewItem.toJson.compactPrint)
          Future.successful(Done)
        case event: NewRouteSpecified =>
          val cargoViewItem = CargoViewItem(
            event.id,
            event.routeSpecification.origin.name,
            event.routeSpecification.destination.name,
            event.routeSpecification.arrivalDeadline
          )
          lmdbClient.put(event.id.idAsString, cargoViewItem.toJson.compactPrint)
          Future.successful(Done)
        case _ =>
          Future.successful(Done)
      }
    } catch {
      case ex: Throwable =>
        logger.error(
          "Unable to process command: " + evt.getClass.getSimpleName + Option(ex.getCause)
            .map(ex => ex.getMessage)
            .getOrElse("") + s" ${ex.getMessage} " + " exception: " + logException(ex),
          ex
        )
        Future.successful(Done)
    }
  }

}

//object CargoViewProjectionWriter
