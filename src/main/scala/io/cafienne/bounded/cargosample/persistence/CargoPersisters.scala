/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.persistence

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{CargoPlanned, NewDeliverySpecified}
import stamina.json.persister

object CargoPersisters {

  import io.cafienne.bounded.cargosample.persistence.CargoDomainEventJsonProtocol._

  val v1CargoPlanned         = persister[CargoPlanned]("cargoplanned")
  val v1NewDeliverySpecified = persister[NewDeliverySpecified]("newdeliveryspecified")

  def persisters = List(
    v1CargoPlanned,
    v1NewDeliverySpecified
  )
}

class CargoPersistersSerializer extends ForwardsCompatibleSerializer(CargoPersisters.persisters) {}
