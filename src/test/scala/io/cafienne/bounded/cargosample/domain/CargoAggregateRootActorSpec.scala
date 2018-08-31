/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.domain

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.util.Timeout
import io.cafienne.bounded.{BuildInfo, RuntimeInfo, UserContext, UserId}
import io.cafienne.bounded.cargosample.domain.Cargo.CargoAggregateState
import io.cafienne.bounded.cargosample.domain.Cargo.CarrierMovement
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol._
import io.cafienne.bounded.aggregate._
import io.cafienne.bounded.cargosample.SpecConfig
import io.cafienne.bounded.test.TestableAggregateRoot
import io.cafienne.bounded.test.TestableAggregateRoot.CommandHandlingException
import org.scalatest._

import scala.concurrent.duration._

class CargoAggregateRootActorSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  implicit val timeout = Timeout(60.seconds) //dilated
  implicit val system  = ActorSystem("CargoTestSystem", SpecConfig.testConfigDVriendInMem)
  implicit val buildInfo =
    BuildInfo(io.cafienne.bounded.cargosample.BuildInfo.name, io.cafienne.bounded.cargosample.BuildInfo.version)
//  implicit val runtimeInfo = RuntimeInfo(System.currentTimeMillis().toString)
  implicit val runtimeInfo = RuntimeInfo("spec")

  //Creation of Aggregate Roots that make use of dependencies is organized via the Creator
  //as a separate class that contains the required dependencies.
  val cargoAggregateRootCreator = new CargoCreator(system, new FixedLocationsProvider())

  val userId1 = CargoUserId(UUID.fromString("53f53841-0bf3-467f-98e2-578d360ee572"))
  val userContext = Some(new UserContext {
    override def roles: List[String] = List.empty

    override def userId: UserId = userId1
  })
  val metaData = CommandMetaData(ZonedDateTime.now(ZoneOffset.UTC), userContext)

  "CargoAggregateRoot" must {

    "Create a new aggregate" in {
      val cargoId2   = CargoId(java.util.UUID.fromString("49A6553D-7E0A-49E8-BE20-925839F524B2"))
      val trackingId = TrackingId(UUID.fromString("53f53841-0bf3-467f-98e2-578d360ee573"))
      val routeSpecification = DeliverySpecification(
        Location("home"),
        Location("destination"),
        ZonedDateTime.parse("2018-03-03T10:15:30+01:00[Europe/Amsterdam]")
      )

      val ar = TestableAggregateRoot
        .given[Cargo, CargoAggregateState](cargoAggregateRootCreator, cargoId2)
        .when(PlanCargo(metaData, cargoId2, trackingId, routeSpecification))

      ar.events should contain(CargoPlanned(MetaData.fromCommand(metaData), cargoId2, trackingId, routeSpecification))
      val targetState = CargoAggregateState(trackingId, routeSpecification)
      ar.currentState map { state =>
        assert(state.get == targetState)
      }
    }

    "Change the delivery specification for an existing Cargo Delivery Using AggregateRootTestFixture" in {
      val cargoId3   = CargoId(java.util.UUID.fromString("D31E3C57-E63E-4AD5-A00B-E5FA9196E80D"))
      val trackingId = TrackingId(UUID.fromString("53f53841-0bf3-467f-98e2-578d360ee573"))
      val routeSpecification = DeliverySpecification(
        Location("home"),
        Location("destination"),
        ZonedDateTime.parse("2018-03-03T10:15:30+01:00[Europe/Amsterdam]")
      )
      val cargoPlannedEvent = CargoPlanned(MetaData.fromCommand(metaData), cargoId3, trackingId, routeSpecification)

      val newDeliverySpecification = DeliverySpecification(
        Location("home"),
        Location("newDestination"),
        ZonedDateTime.parse("2018-03-04T10:45:45+01:00[Europe/Amsterdam]")
      )
      val specifyNewDeliveryCommand = SpecifyNewDelivery(metaData, cargoId3, newDeliverySpecification)

      val ar = TestableAggregateRoot
        .given[Cargo, CargoAggregateState](cargoAggregateRootCreator, cargoId3, cargoPlannedEvent)
        .when(specifyNewDeliveryCommand)

      // You see that this only shows the events that are 'published' via when
      ar.events should contain(NewDeliverySpecified(MetaData.fromCommand(metaData), cargoId3, newDeliverySpecification))

      val targetState = CargoAggregateState(trackingId, newDeliverySpecification)
      ar.currentState map { state =>
        assert(state.get == targetState)
      }
    }

    "Load a cargo for an Iterary" in {
      val cargoId3        = CargoId(java.util.UUID.fromString("D31E3C57-E63E-4AD5-A00B-E5FA9196E80D"))
      val trackingId      = TrackingId(UUID.fromString("53f53841-0bf3-467f-98e2-578d360ee573"))
      val deliveryDueDate = ZonedDateTime.parse("2018-03-03T10:15:30+01:00[Europe/Amsterdam]")
      val deliverySpecification = DeliverySpecification(
        Location("home"),
        Location("destination"),
        ZonedDateTime.parse("2018-03-03T10:15:30+01:00")
      )
      val cargoPlannedEvent = CargoPlanned(MetaData.fromCommand(metaData), cargoId3, trackingId, deliverySpecification)
      val vesselVoyageId    = VesselVoyageId(UUID.fromString("AC1000CD-20FE-48B2-8828-F51F1C3114C4"))
      val loadCargo = Loading(
        metaData,
        cargoId3,
        Location("amsterdam"),
        vesselVoyageId
      )

      val ar = TestableAggregateRoot
        .given[Cargo, CargoAggregateState](cargoAggregateRootCreator, cargoId3, cargoPlannedEvent)
        .when(loadCargo)

      // You see that this only shows the events that are 'published' via when
      ar.events should contain(Loaded(MetaData.fromCommand(metaData), cargoId3, Location("amsterdam"), vesselVoyageId))
      val targetState = CargoAggregateState(trackingId, deliverySpecification, Some(CarrierMovement(vesselVoyageId)))
      ar.currentState map { state =>
        assert(state.get == targetState)
      }
    }

    "Cannot load a cargo for an Iterary" in {
      val cargoId3        = CargoId(java.util.UUID.fromString("D31E3C57-E63E-4AD5-A00B-E5FA9196E80D"))
      val trackingId      = TrackingId(UUID.fromString("53f53841-0bf3-467f-98e2-578d360ee573"))
      val deliveryDueDate = ZonedDateTime.parse("2018-03-03T10:15:30+01:00[Europe/Amsterdam]")
      val deliverySpecification = DeliverySpecification(
        Location("home"),
        Location("destination"),
        ZonedDateTime.parse("2018-03-03T10:15:30+01:00")
      )
      val cargoPlannedEvent = CargoPlanned(MetaData.fromCommand(metaData), cargoId3, trackingId, deliverySpecification)
      val vesselVoyageId    = VesselVoyageId(UUID.fromString("AC1000CD-20FE-48B2-8828-F51F1C3114C4"))
      val cargoLoadedEvent = Loaded(
        MetaData.fromCommand(metaData),
        cargoId3,
        Location("amsterdam"),
        vesselVoyageId
      )

      val newLoad = Loading(
        metaData,
        cargoId3,
        Location("antwerp"),
        VesselVoyageId(UUID.fromString("629167F9-2095-485F-B3E2-D38FDEB7A345"))
      )

      an[CommandHandlingException] should be thrownBy {
        val ar = TestableAggregateRoot
          .given[Cargo, CargoAggregateState](cargoAggregateRootCreator, cargoId3, cargoPlannedEvent, cargoLoadedEvent)
          .when(newLoad)
      }
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system, 30.seconds, verifySystemShutdown = true)
  }

}
