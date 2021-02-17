/*
 * Copyright (C) 2018-2021  Cafienne B.V.
 */

package io.cafienne.bounded.cargosample.domain

import java.util.UUID
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol._
import spray.json.{RootJsonFormat, _}

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object CargoDomainJsonProtocol extends DefaultJsonProtocol {

  def jsonEnum[T <: Enumeration](enu: T): JsonFormat[T#Value] =
    new JsonFormat[T#Value] {
      def write(obj: T#Value): JsValue = JsString(obj.toString)

      def read(json: JsValue): T#Value =
        json match {
          case JsString(txt) => enu.withName(txt)
          case something =>
            throw DeserializationException(s"Expected a value from enum $enu instead of $something")
        }
    }

  implicit object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {

    def write(dt: OffsetDateTime): JsValue =
      JsString(
        dt.truncatedTo(ChronoUnit.SECONDS)
          .format(DateTimeFormatter.ISO_DATE_TIME)
      )

    def read(value: JsValue): OffsetDateTime =
      value match {
        case JsString(v) =>
          OffsetDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME)
        case _ =>
          deserializationError(s"value $value not conform ISO8601 (yyyy-MM-dd'T'HH:mm:ssZZ) where time is optional")
      }
  }

  implicit object JavaUUIDFormat extends RootJsonFormat[UUID] {
    override def write(obj: UUID): JsValue = JsString(obj.toString)

    override def read(json: JsValue): UUID =
      json match {
        case JsString(v) => UUID.fromString(v)
        case _ =>
          deserializationError(s"value $json cannot be deserialized to a UUID")
      }
  }

  implicit object cargoUserIdFmt extends RootJsonFormat[CargoUserId] {
    override def write(obj: CargoUserId): JsValue = JsString(obj.id.toString)

    override def read(json: JsValue): CargoUserId =
      json match {
        case JsString(v) => CargoUserId(UUID.fromString(v))
        case _ =>
          deserializationError(s"value $json cannot be deserialized to a CargoUserId")
      }
  }

  implicit object vesselVoyageIdFmt extends RootJsonFormat[VesselVoyageId] {
    override def write(obj: VesselVoyageId): JsValue = JsString(obj.id.toString)

    override def read(json: JsValue): VesselVoyageId =
      json match {
        case JsString(v) => VesselVoyageId(UUID.fromString(v))
        case _ =>
          deserializationError(s"value $json cannot be deserialized to a VesselVoyageId")
      }
  }

  implicit object cargoIdFmt extends RootJsonFormat[CargoId] {
    override def write(obj: CargoId): JsValue = JsString(obj.id.toString)

    override def read(json: JsValue): CargoId =
      json match {
        case JsString(v) => CargoId(UUID.fromString(v))
        case _ =>
          deserializationError(s"value $json cannot be deserialized to a CargoId")
      }
  }

//  implicit object CargoUserContextJsonFormat extends RootJsonFormat[UserContext] {
//    override def write(obj: UserContext): JsValue =
//      JsObject(
//        "userId" -> JsString(obj.userId.idAsString),
//        "roles"  -> JsArray(obj.roles.map(r => JsString(r)).toVector)
//      )
//
//    override def read(json: JsValue): UserContext =
//      json match {
//        case JsObject(fields) if fields.contains("userId") =>
//          (fields("userId"), fields("roles")) match {
//            case (JsString(userStr), JsArray(rolesArr)) =>
//              val userId = CargoUserId(UUID.fromString(userStr))
//              val roles  = rolesArr.map(r => r.toString()).toList
//              CargoUserContext(userId, roles)
//            case _ =>
//              deserializationError(s"value $json does not conform the UserContext json object")
//          }
//      }
//  }
  implicit val CargoUserContextFmt            = jsonFormat2(CargoUserContext)
  implicit val CargoCommandMetaDataJsonFormat = jsonFormat3(CargoCommandMetaData)
  implicit val MetaDataJsonFormat             = jsonFormat3(CargoMetaData.apply)

  implicit object chargeSessionIdFmt extends RootJsonFormat[TrackingId] {
    override def write(obj: TrackingId): JsValue = JsString(obj.id.toString)

    override def read(json: JsValue): TrackingId =
      json match {
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
