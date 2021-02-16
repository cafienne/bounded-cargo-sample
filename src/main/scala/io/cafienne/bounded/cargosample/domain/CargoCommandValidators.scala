/*
 * Copyright (C) 2018-2021  Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.domain

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.cafienne.bounded.akka.ActorSystemProvider
import io.cafienne.bounded.akka.persistence.ReadJournalProvider
import io.cafienne.bounded.aggregate.ValidateableCommand
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.{
  CargoDomainCommand,
  CargoNotFound,
  PlanCargo,
  SpecifyNewDelivery
}

import scala.concurrent.Future

trait CargoCommandValidators {
  implicit val PlanCargoValidator: ValidateableCommand[PlanCargo]
  implicit val SpecifyNewDeliveryValidator: ValidateableCommand[SpecifyNewDelivery]
}

trait ExistenceChecker extends ActorSystemProvider with ReadJournalProvider {
  implicit val materializer = ActorMaterializer()
  implicit val ec           = system.dispatcher

  def exists[A <: CargoDomainCommand](cmd: A): Future[A] =
    readJournal
      .currentEventsByPersistenceId(cmd.aggregateRootId, 0, 1)
      .runFold(false)((_, _) => true)
      .flatMap(f =>
        if (f) Future.successful(cmd)
        else
          Future
            .failed(CargoNotFound(s"Cargo with id ${cmd.aggregateRootId} not found while processing command $cmd"))
      )
}

class CargoCommandValidatorsImpl(actorSystem: ActorSystem) extends CargoCommandValidators {

  implicit val PlanCargoValidator = new PlanCargoValidator()
  implicit val SpecifyNewDeliveryValidator =
    new CargoCommandValidator[SpecifyNewDelivery](actorSystem)
}

class PlanCargoValidator() extends ValidateableCommand[PlanCargo] {
  override def validate(cmd: PlanCargo): Future[PlanCargo] = {
    Future.successful(cmd)
  }
}

class CargoCommandValidator[T <: CargoDomainCommand](actorSystem: ActorSystem)
    extends ValidateableCommand[T]
    with ExistenceChecker {
  override implicit def system: ActorSystem = actorSystem

  override def validate(cmd: T): Future[T] = exists[T](cmd)
}
