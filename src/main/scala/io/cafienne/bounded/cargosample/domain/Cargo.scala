/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.domain

import akka.actor._
import io.cafienne.bounded.{BuildInfo, RuntimeInfo}
import io.cafienne.bounded.aggregate._
import io.cafienne.bounded.cargosample.domain.Cargo.CargoAggregateState
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol._

import scala.collection.immutable.Seq

/**
  * Aggregate root that keeps the logic of the cargo.
  * @param cargoId unique identifier for cargo.
  */
class Cargo(
  cargoId: AggregateRootId,
  locationsProvider: LocationsProvider,
  buildInfo: BuildInfo,
  runtimeInfo: RuntimeInfo
) extends AggregateRootActor[CargoAggregateState] {

  implicit val bi = buildInfo
  implicit val ri = runtimeInfo

  override def aggregateId: AggregateRootId = cargoId

  override def handleCommand(command: DomainCommand, state: Option[CargoAggregateState]): Reply = {
    command match {
      case cmd: PlanCargo =>
        Ok(
          Seq(CargoPlanned(MetaData.fromCommand(cmd.metaData), cmd.cargoId, cmd.trackingId, cmd.deliverySpecification))
        )
      case cmd: SpecifyNewDelivery =>
        Ok(Seq(NewDeliverySpecified(MetaData.fromCommand(cmd.metaData), cmd.cargoId, cmd.deliverySpecification)))
      case cmd: Loading =>
        if (state.isDefined && state.get.currentCarrierMovement.isDefined) {
          Ko(LoadingFailure(s"Cannot load a new Cargo as there is already a shipment loaded $state"))
        } else {
          Ok(Seq(Loaded(MetaData.fromCommand(cmd.metaData), cmd.cargoId, cmd.location, cmd.vesselVoyageId)))
        }
      case cmd: Unloading =>
        Ok(Seq(Unloaded(MetaData.fromCommand(cmd.metaData), cmd.cargoId, cmd.location, cmd.vesselVoyageId)))
      case other => Ko(new UnexpectedCommand(other))
    }
  }

  override def newState(evt: DomainEvent): Option[CargoAggregateState] = {
    evt match {
      case evt: CargoPlanned =>
        Some(CargoAggregateState(evt.trackingId, evt.deliverySpecification))
      case _ =>
        throw new IllegalArgumentException(s"Event $evt is not valid to create a new CargoAggregateState")
    }
  }

}

object Cargo {

  case class CarrierMovement(versselVoyageId: VesselVoyageId)

  case class CargoAggregateState(
    trackingId: TrackingId,
    deliverySpecification: DeliverySpecification,
    currentCarrierMovement: Option[CarrierMovement] = None
  ) extends AggregateState[CargoAggregateState] {

    override def update(evt: DomainEvent): Option[CargoAggregateState] = {
      evt match {
        case CargoPlanned(_, _, newTrackingId, newDeliverySpecification) =>
          Some(CargoAggregateState(newTrackingId, newDeliverySpecification))
        case NewDeliverySpecified(_, _, newDeliverySpecification) =>
          Some(this.copy(deliverySpecification = newDeliverySpecification))
        case Loaded(_, _, _, vesselVoyageId) =>
          Some(this.copy(currentCarrierMovement = Some(CarrierMovement(vesselVoyageId))))
        case Unloaded(_, _, _, _) =>
          Some(this.copy(currentCarrierMovement = None))
        case _ => Some(this)
      }
    }
  }

  final val aggregateRootTag = "ar-cargo" // used to tag the events and read them

}

/**
  * The Aggregate Root needs dependencies. These are given via the Creator.
  * A Creator returns the props that is used to create an Aggregate Root Actor.
  * @param system as a sample dependency the actor system is passed.
  * @param locations a dependency is used inside the Aggregate Root.
  */
class CargoCreator(system: ActorSystem, locations: LocationsProvider)(
  implicit buildInfo: BuildInfo,
  runtimeInfo: RuntimeInfo
) extends AggregateRootCreator {

  override def props(cargoId: AggregateRootId): Props = {
    system.log.debug("Returning new Props for {}", cargoId)
    Props(classOf[Cargo], cargoId, locations, buildInfo, runtimeInfo)
  }

}
