# simex-web-service
A REST web service for handling SIMEX messages

## Overview
In order to use this library within a project:
* Implement the security service ```SimexMessageSecurityServiceAlgebra.scala```
* Implement the endpoint handler ```SimexMessageHandlerAlgebra.scala```
* Use the return from ```new HttpRouteResource[F]().routes(endpointHandler)``` in your HTTP App

## Security Service ```SimexMessageSecurityServiceAlgebra```
This should check the security credentials of a SIMEX message by implementing the following method:
```scala
def handleSimexRequest(respond: SecurityResponseResource.SecurityResponse.type)(
      body: Simex
  ): F[SecurityResponse]
```
The ```SecurityResponse``` is described in ```SecurityResponseResource.scala``` and can be one of two values:
* ```SecurityPassed``` - where the handler will continue with handling the SIMEX request
* ```SecurityFailed``` - where the handler will halt and return a `Forbidden` response

## HTTP Endpoint SIMEX Message Request Handler ```SimexMessageHandlerAlgebra```
This is an abstract class with the following method:
```scala
  def handleSimexRequest(respond: HttpResponseResource.HttpResponse.type)(
      body: Simex
  ): F[HttpResponseResource.HttpResponse]
```
Basically, this should hand off the SIMEX message ```body``` to the service orchestrator.

This class takes two arguments:
* The URL that this endpoint handles
* An implementation of ```SimexMessageSecurityServiceAlgebra```

## Wiring the services
Once the above has been done, then:
```scala
val endpointRoute = new HttpRouteResource[F]().routes(endpointHandler)
val allRoutes = (otherRoutes <+> endpointRoute).orNotFound
val httpApp = Logger.httpApp(logHeaders = true, logBody = true)(allRoutes)
```
and then build your HTTP server.



