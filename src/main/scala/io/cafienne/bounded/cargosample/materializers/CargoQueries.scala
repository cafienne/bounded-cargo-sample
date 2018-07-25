/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.materializers

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.CargoId

trait CargoQueries {
  import QueriesJsonProtocol._

  def getCargo(cargoId: CargoId): Option[CargoViewItem]

}
