/*
 * Copyright (C) 2018-2021  Creative Commons CC0 1.0 Universal
 */

package io.cafienne.bounded.cargosample

import com.typesafe.config.ConfigFactory

object SpecConfig {

  /*
  PLEASE NOTE:
  Currently the https://github.com/dnvriend/akka-persistence-inmemory is NOT working for Aggregate Root tests
  because it is not possible to use a separate instance writing the events that should be in the event store
  before you actually create the aggregate root (should replay those stored events) to check execution of a new
  command.
  A new configuration that uses the akka bundled inmem storage is added to create a working situation.
   */
  val testConfigDVriendInMem = ConfigFactory.parseString(
    """
      |      akka {
      |        loglevel = "DEBUG"
      |        stdout-loglevel = "DEBUG"
      |        loggers = ["akka.testkit.TestEventListener"]
      |        actor {
      |           serialize-creators = off
      |           serialize-messages = off
      |           serialization-bindings {
      |             "io.cafienne.bounded.aggregate.DomainEvent" = jackson-json
      |             // enable below to check if all taskEvents have been serialized without java.io.Serializable
      |             "java.io.Serializable" = none
      |           }
      |        }
      |      persistence {
      |       publish-confirmations = on
      |       publish-plugin-commands = on
      |       journal {
      |          plugin = "inmemory-journal"
      |       }
      |       snapshot-store.plugin = "inmemory-snapshot-store"
      |      }
      |      test {
      |        single-expect-default = 10s
      |        timefactor = 1
      |      }
      |    }
      |    inmemory-journal {
      |      event-adapters {
      |        cargoTagging = "io.cafienne.bounded.cargosample.persistence.CargoTaggingEventAdapter"
      |      }
      |      event-adapter-bindings {
      |        "io.cafienne.bounded.cargosample.domain.CargoDomainProtocol$CargoDomainEvent" = cargoTagging
      |      }
      |    }
      |    inmemory-read-journal {
      |      refresh-interval = "10ms"
      |      max-buffer-size = "1000"
      |    }
      |
      |    bounded.eventmaterializers.publish = true
      |
      |    bounded.eventmaterializers.offsetstore {
      |       type = "inmemory"
      |   }
    """.stripMargin
  )

  /*
   * This configuration works for Aggregate Root Testing. (see above for explanation)
   */
  val testConfigAkkaInMem = ConfigFactory.parseString(
    """
      |      akka {
      |        loglevel = "DEBUG"
      |        stdout-loglevel = "DEBUG"
      |        loggers = ["akka.testkit.TestEventListener"]
      |        actor {
      |           serialize-creators = off
      |           serialize-messages = off
      |           serialization-bindings {
      |             "io.cafienne.bounded.aggregate.DomainEvent" = jackson-json
      |             // enable below to check if all taskEvents have been serialized without java.io.Serializable
      |             "java.io.Serializable" = none
      |           }
      |        }
      |      persistence {
      |       publish-confirmations = on
      |       publish-plugin-commands = on
      |       journal {
      |          plugin = "inmemory-journal"
      |       }
      |      }
      |      test {
      |        single-expect-default = 10s
      |        timefactor = 1
      |      }
      |    }
      |
      |    bounded.eventmaterializers.publish = true
      |
      |    bounded.eventmaterializers.offsetstore {
      |       type = "inmemory"
      |   }
    """.stripMargin
  )

}
