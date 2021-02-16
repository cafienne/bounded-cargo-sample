/*
 * Copyright (C) 2018-2021  Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.persistence

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{CargoPlanned, Loaded, NewDeliverySpecified, Unloaded}
import stamina.json.persister

object CargoPersisters {

  import io.cafienne.bounded.cargosample.persistence.CargoDomainEventJsonProtocol._

  val v1CargoPlanned         = persister[CargoPlanned]("cargoplanned")
  val v1CargoLoaded          = persister[Loaded]("loaded")
  val v1CargoUnloaded        = persister[Unloaded]("unloaded")
  val v1NewDeliverySpecified = persister[NewDeliverySpecified]("newdeliveryspecified")

  def persisters =
    List(
      v1CargoPlanned,
      v1CargoLoaded,
      v1CargoUnloaded,
      v1NewDeliverySpecified
    )
}

class CargoPersistersSerializer extends ForwardsCompatibleSerializer(CargoPersisters.persisters) {}
