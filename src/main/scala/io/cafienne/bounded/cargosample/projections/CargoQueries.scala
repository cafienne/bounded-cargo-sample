/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.projections

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.CargoId

import scala.concurrent.Future

trait CargoQueries {
  import QueriesJsonProtocol._

  def getCargo(cargoId: CargoId): Option[CargoViewItem]

}
