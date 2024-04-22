# simex-web-service (1.0.0)
A REST web service for handling SIMEX messages

## Overview
In order to use this library within a project:
* Implement the security service ```SimexMessageSecurityServiceAlgebra.scala```
* Implement the SIMEX request validator ```SimexRequestValidatorAlgebra```
* Implement the endpoint handler ```SimexMessageHandlerAlgebra.scala```
* Use the return from ```new HttpRouteResource[F]().routes(endpointHandler)``` in your HTTP App

## Security Service ```SimexMessageSecurityServiceAlgebra```
This should check the security credentials of a SIMEX message by implementing the following method:
```scala
def checkSecurityForRequest(request: Simex): F[SecurityResponse]
```
The ```SecurityResponse``` is described in ```SecurityResponseResource.scala``` and can be one of two values:
* ```SecurityPassed``` - where the handler will continue with handling the SIMEX request
* ```SecurityFailed``` - where the handler will halt and return a `Forbidden` response

## Request Validator ```SimexRequestValidatorAlgebra```
This should check the request is a valid request and that it has passed all the necessary data for the service by
implementing the following method:
```scala
def validateRequest(request: Simex): F[Validation]
```
The ```Validation``` response is described in ```Validation.scala```  indicating that either it has passed (i.e. all 
the data was given) or failed.

## HTTP Endpoint SIMEX Message Request Handler ```SimexMessageHandlerAlgebra```
This is an abstract class with the one abstract method:
```scala
  def handleValidatedSimexRequest(request: Simex): F[HttpResponse]
```
Basically, this should hand off the SIMEX message ```request``` to the service orchestrator. The other methods
in this class are declared as *final* as the guarantee that the request has been checked first for security, and then
validation.

This class takes three arguments:
* The URL that this endpoint handles
* An implementation of ```SimexMessageSecurityServiceAlgebra```
* An implementation of ```SimexRequestValidatorAlgebra```

## Wiring the services
Once the above has been done, then:
```scala
val endpointRoute = new HttpRouteResource[F]().routes(endpointHandler)
val allRoutes = (otherRoutes <+> endpointRoute).orNotFound
val httpApp = Logger.httpApp(logHeaders = true, logBody = true)(allRoutes)
```
and then build your HTTP server.



