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

## Cargo Sample of the [Blue Book](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215)

### Modeling the Cargo Sample NOTES

The Cargo sample is an extended example with a lot of detail, this sample is used to code the model is described and shown in this chapter.
Sometimes there may be differences, these will be discussed over here.

The CargoDomainProtocol contains the messages as case classes to deal with the domain. Bounded is modeled in an action driven way instead
of a data driven way. Based on the actions, the intent, consequences are modeled as the event.

Next to that, bounded creates an event sourced system and by storing events for Handling, this automatically creates a Delivery History
that is not required as a separate Object and Storage model.

With regards to the model, there is one named Customer that personally seems a bit odd as that is used to store all parties involved during
shipping and handling of the cargo. We respect the described domain and will use the Customer anyway.

