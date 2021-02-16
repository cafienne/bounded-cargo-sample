/*
 * Copyright (C) 2018-2021  Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.eventmaterializers

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.CargoId

trait CargoQueries {
  import QueriesJsonProtocol._

  def getCargo(cargoId: CargoId): Option[CargoViewItem]

}
