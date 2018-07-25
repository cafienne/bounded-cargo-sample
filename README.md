# Cargo Sample Application 

This sample shows the use of the bounded framework and is based on the Cargo Sample as found in the blue book of Eric Evans.

The application is based on the scala programming language and makes use of the [bounded framework](https://cafienne.io/bounded)
The bounded framework is built on top of the [Akka](https://akka.io) actor framework.

This allows the application to be reactive and consists of the following structure:

```
                 ┌─────────┐   ┌──────────────────────┐
                 │         │   │      DOMAIN          │
┌──────┐         │ Command │   │        ┌───────┐     │
│      │         │ Routing │   │        │       │     │
│      │─────────▶         ├───┼────────▶ CARGO │─────┼────────────────┐
│      │         │         │   │        │ AR    │     │                │
│ HTTP │         │         │   │        └───────┘     │                ▼
│ API  │         │         │   │                      │       ┌────────────────┐
│      │         └─────────┘   │                      │       │   PERSISTENCE  │
│      │                       │                      │       │                │
│      │                       │                      │       │                │
│      │◀──────────┐           └──────────────────────┘       │   EVENT        │
│      │           │                                          │   STORE        │
│      │           │                                          │                │
│      │           │           ┌──────────────────────┐       │                │
└──────┘           │           │   Materializers      │       │                │
                   │           │┌─────────┬─────────┐ │       │                │
                   │           ││ Cargo   │ Cargo   │ │       └────────────────┘
                   │           ││ Item    │ Item    │ │                │
                   └───────────┤│ Query   │ Writer  │◀┼────────────────┘
                               ││Interface│         │ │
                               │└─────────┴─────────┘ │
                               │                      │
                               │                      │
                               │                      │
                               └──────────────────────┘
```

The package ```io.cafienne.bounded.cargosample``` contains the complete structure as drawn above:

 * the ```root``` contains the Boot used to start the HTTP service and will wire all dependencies
 * ```domain``` contains the business logic and will transform commands (intent) to events (result)
 * ```httpapi``` contains an akka-http DSL based webserver that will expose the service to the outside world.
   The endpoint is documented with Swagger
 * ```materializers``` contains the parts that are constructed based on the stored events, like the query side of the API
 * ```persistence``` contains support for serialization and handling event versions of the stored events

## Run the application

The ```src/main/resources/application.conf``` is setup to use a file based event store and file based offset store.
You can start the application by:
```bash
$ sbt run
```
