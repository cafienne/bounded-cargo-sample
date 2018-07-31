/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.persistence

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{CargoPlanned, NewDeliverySpecified}
import spray.json._

object CargoDomainEventJsonProtocol extends DefaultJsonProtocol {
  import io.cafienne.bounded.cargosample.domain.CargoDomainJsonProtocol._

  implicit val cargoPlannedFmt         = jsonFormat4(CargoPlanned)
  implicit val newDeliverySpecifiedFmt = jsonFormat3(NewDeliverySpecified)

}
