/*
 * Copyright (C) 2018 Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample.persistence

import akka.persistence.journal.{Tagged, WriteEventAdapter}
import io.cafienne.bounded.cargosample.domain.Cargo
import io.cafienne.bounded.cargosample.domain.CargoDomainProtocol.CargoDomainEvent

class CargoTaggingEventAdapter extends WriteEventAdapter {
  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = event match {
    case prEvent: CargoDomainEvent =>
      Tagged(prEvent, Set(Cargo.aggregateRootTag))
    case other => other
  }
}
