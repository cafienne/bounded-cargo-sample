/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.httpapi

import java.util.UUID

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.DeliverySpecification
import spray.json._

/**
  * JSON protocol used by the http API.
  * Contains specific protocol messages and the JSON serialization instructions for the Spray JSON format
  * for all classes used with the http API.
  */
object HttpJsonProtocol extends DefaultJsonProtocol {
  import io.cafienne.bounded.aggregate.ProtocolJsonProtocol._
  import io.cafienne.bounded.cargosample.domain.CargoDomainJsonProtocol._

  case class PlanCargo(trackingId: UUID, deliverySpecification: DeliverySpecification)

  implicit val planCargoFmt = jsonFormat2(PlanCargo)

  case class ErrorResponse(msg: String)

  implicit val errorResponsFmt = jsonFormat1(ErrorResponse)

}
