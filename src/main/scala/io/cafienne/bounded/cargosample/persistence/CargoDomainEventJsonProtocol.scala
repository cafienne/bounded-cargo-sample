/*
 * Copyright (C) 2018-2021  Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.persistence

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{CargoPlanned, Loaded, NewDeliverySpecified, Unloaded}
import spray.json._

object CargoDomainEventJsonProtocol extends DefaultJsonProtocol {
  import io.cafienne.bounded.cargosample.domain.CargoDomainJsonProtocol._

  implicit val cargoPlannedFmt         = jsonFormat4(CargoPlanned)
  implicit val cargoLoadedFmt          = jsonFormat4(Loaded)
  implicit val cargoUnloadedFmt        = jsonFormat4(Unloaded)
  implicit val newDeliverySpecifiedFmt = jsonFormat3(NewDeliverySpecified)
}
