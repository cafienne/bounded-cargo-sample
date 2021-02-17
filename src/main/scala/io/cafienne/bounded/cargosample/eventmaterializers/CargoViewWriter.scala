/*
 * Copyright (C) 2018-2021  Cafienne B.V.
 */

package io.cafienne.bounded.cargosample.eventmaterializers

import akka.Done
import akka.actor.ActorSystem
import io.cafienne.bounded.eventmaterializers.AbstractReplayableEventMaterializer
import com.typesafe.scalalogging.Logger
import io.cafienne.bounded.cargosample.domain.Cargo
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{CargoPlanned, NewDeliverySpecified}
import io.cafienne.bounded.cargosample.eventmaterializers.QueriesJsonProtocol.CargoViewItem
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class CargoViewWriter(actorSystem: ActorSystem, lmdbClient: LmdbClient)
    extends AbstractReplayableEventMaterializer(actorSystem) {

  /**
    * Tagname used to identify eventstream to listen to
    */
  override val tagName: String = Cargo.aggregateRootTag

  /**
    * Mapping name of this listener
    */
  override val matMappingName: String = "cargo-view"

  override lazy val logger: Logger = Logger(LoggerFactory.getLogger(CargoViewWriter.this.getClass))

  override def handleReplayEvent(evt: Any): Future[Done] = handleEvent(evt)

  override def handleEvent(evt: Any): Future[Done] = {
    try {
      evt match {
        case event: CargoPlanned =>
          val cargoViewItem = CargoViewItem(
            event.cargoId.idAsString,
            event.deliverySpecification.origin.name,
            event.deliverySpecification.destination.name,
            event.deliverySpecification.arrivalDeadline
          )
          lmdbClient.put(event.cargoId.idAsString, cargoViewItem.toJson.compactPrint)
          Future.successful(Done)
        case event: NewDeliverySpecified =>
          val cargoViewItem = CargoViewItem(
            event.id,
            event.deliverySpecification.origin.name,
            event.deliverySpecification.destination.name,
            event.deliverySpecification.arrivalDeadline
          )
          lmdbClient.put(event.id, cargoViewItem.toJson.compactPrint)
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
