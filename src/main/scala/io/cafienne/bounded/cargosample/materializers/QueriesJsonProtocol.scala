/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.materializers

import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.CargoId
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import java.time.ZonedDateTime
import scala.annotation.meta.field
import spray.json._

object QueriesJsonProtocol extends DefaultJsonProtocol {
  import io.cafienne.bounded.cargosample.domain.CargoDomainJsonProtocol._
  import io.cafienne.bounded.aggregate.ProtocolJsonProtocol._
  @ApiModel(description = "Details of a single Cargo")
  case class CargoViewItem(
    @(ApiModelProperty @field)(value = "Unique identifier of this Cargo", required = true, dataType = "string") id: CargoId,
    @(ApiModelProperty @field)(value = "origin", required = true, dataType = "string") origin: String,
    @(ApiModelProperty @field)(value = "destination", required = true, dataType = "string") destination: String,
    @(ApiModelProperty @field)(value = "delivery due date", required = true, dataType = "string") deliveryDueDate: ZonedDateTime
  )

  implicit val cargoViewItemFmt = jsonFormat4(CargoViewItem)

}
