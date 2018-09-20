/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.domain

import java.util.UUID

import io.cafienne.bounded.UserContext
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol._
import spray.json.{RootJsonFormat, _}

object CargoDomainJsonProtocol extends DefaultJsonProtocol {
  import io.cafienne.bounded.aggregate.ProtocolJsonProtocol._

  implicit object cargoUserIdFmt extends RootJsonFormat[CargoUserId] {
    override def write(obj: CargoUserId): JsValue = JsString(obj.id.toString)

    override def read(json: JsValue): CargoUserId = json match {
      case JsString(v) => CargoUserId(UUID.fromString(v))
      case _ =>
        deserializationError(s"value $json cannot be deserialized to a CargoUserId")
    }
  }

  implicit object vesselVoyageIdFmt extends RootJsonFormat[VesselVoyageId] {
    override def write(obj: VesselVoyageId): JsValue = JsString(obj.id.toString)

    override def read(json: JsValue): VesselVoyageId = json match {
      case JsString(v) => VesselVoyageId(UUID.fromString(v))
      case _ =>
        deserializationError(s"value $json cannot be deserialized to a VesselVoyageId")
    }
  }

  implicit object cargoIdFmt extends RootJsonFormat[CargoId] {
    override def write(obj: CargoId): JsValue = JsString(obj.id.toString)

    override def read(json: JsValue): CargoId = json match {
      case JsString(v) => CargoId(UUID.fromString(v))
      case _ =>
        deserializationError(s"value $json cannot be deserialized to a CargoId")
    }
  }

  implicit object CargoUserContextJsonFormat extends RootJsonFormat[UserContext] {
    override def write(obj: UserContext): JsValue = JsObject(
      "userId" -> JsString(obj.userId.idAsString),
      "roles"  -> JsArray(obj.roles.map(r => JsString(r)).toVector)
    )

    override def read(json: JsValue): UserContext = json match {
      case JsObject(fields) if fields.contains("userId") =>
        (fields("userId"), fields("roles")) match {
          case (JsString(userStr), JsArray(rolesArr)) =>
            val userId = CargoUserId(UUID.fromString(userStr))
            val roles  = rolesArr.map(r => r.toString()).toList
            CargoUserContext(userId, roles)
          case _ =>
            deserializationError(s"value $json does not conform the UserContext json object")
        }
    }
  }

  implicit val CargoCommandMetaDataJsonFormat = jsonFormat3(CargoCommandMetaData)
  implicit val MetaDataJsonFormat             = jsonFormat5(CargoMetaData.apply)

  implicit object chargeSessionIdFmt extends RootJsonFormat[TrackingId] {
    override def write(obj: TrackingId): JsValue = JsString(obj.id.toString)

    override def read(json: JsValue): TrackingId = json match {
      case JsString(v) => TrackingId(UUID.fromString(v))
      case _ =>
        deserializationError(s"value $json cannot be deserialized to a TrackingId")
    }
  }

  implicit val locationFmt              = jsonFormat1(Location)
  implicit val deliverySpecificationFmt = jsonFormat3(DeliverySpecification)

  implicit object CargoNotFoundFmt extends RootJsonFormat[CargoNotFound] {
    override def read(json: JsValue): CargoNotFound =
      json.asJsObject.getFields("message", "cause") match {
        case Seq(JsString(message), JsString(cause)) =>
          CargoNotFound(message, new Throwable(cause))
        case Seq(JsString(message), JsNull) => CargoNotFound(message)
      }

    override def write(obj: CargoNotFound): JsValue =
      JsObject(
        Map("message" -> JsString(obj.msg)).++:(
          Option(obj.getCause)
            .fold(Map.empty[String, JsValue])(cause => Map("cause" -> JsString(cause.getMessage)))
        )
      )
  }

  implicit val planCargoFmt          = jsonFormat4(PlanCargo)
  implicit val specifyNewDeliveryFmt = jsonFormat3(SpecifyNewDelivery)

}
