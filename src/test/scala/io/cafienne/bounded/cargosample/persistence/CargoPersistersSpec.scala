/*
 * Copyright (C) 2018-2021  Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.persistence

import java.time.{OffsetDateTime, ZonedDateTime}
import java.util.UUID
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol._
import org.scalatest._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import stamina.Persisters
import stamina.testkit._

class CargoPersistersSpec extends AnyWordSpec with Matchers with StaminaTestKit {

  val persisters = Persisters(CargoPersisters.persisters)

  val userId           = CargoUserId(UUID.fromString("53f53841-0bf3-467f-98e2-578d360ee572"))
  val timestamp        = OffsetDateTime.parse("2018-02-02T10:15:30+01:00")
  val cargoUserContext = CargoUserContext(userId, List.empty)
  val metaData =
    CargoCommandMetaData(timestamp, Some(cargoUserContext), UUID.fromString("60f5b725-799e-423d-8e70-0a664b1e0963"))

  "The Cargo persister" should {
    val cargoId    = CargoId(java.util.UUID.fromString("D31E3C57-E63E-4AD5-A00B-E5FA9196E80D"))
    val trackingId = TrackingId(UUID.fromString("53f53841-0bf3-467f-98e2-578d360ee573"))
    val routeSpecification = DeliverySpecification(
      Location("home"),
      Location("destination"),
      OffsetDateTime.parse("2018-03-03T10:15:30+01:00")
    )

    val cargoPlannedEvent = CargoPlanned(CargoMetaData.fromCommand(metaData), cargoId, trackingId, routeSpecification)
    val loadedEvent = Loaded(
      CargoMetaData.fromCommand(metaData),
      cargoId,
      Location("AMS"),
      VesselVoyageId(UUID.fromString("7FB3CBB1-1282-4D7D-BE62-D990732EC1E9"))
    )
    val unloadedEvent = Unloaded(
      CargoMetaData.fromCommand(metaData),
      cargoId,
      Location("AMS"),
      VesselVoyageId(UUID.fromString("7FB3CBB1-1282-4D7D-BE62-D990732EC1E9"))
    )
    val newDeliverySpecified = NewDeliverySpecified(CargoMetaData.fromCommand(metaData), cargoId, routeSpecification)

    persisters.generateTestsFor(
      sample(cargoPlannedEvent),
      sample(loadedEvent),
      sample(unloadedEvent),
      sample(newDeliverySpecified)
    )
  }
}
