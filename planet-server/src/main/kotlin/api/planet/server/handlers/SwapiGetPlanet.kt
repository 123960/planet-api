package api.planet.server.handlers

import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

import api.planet.server.swapi.SwapiClient

class SwapiGetPlanet : Handler<RoutingContext> {

  companion object {
    val log = LoggerFactory.getLogger(SwapiGetPlanet::class.java)
  } 

  override fun handle(routingContext: RoutingContext) {
    try {
      var params: RequestParameters = routingContext.get("parsedParameters")
      log.info("[params: ${params.toJson()}]")
      val future  = SwapiClient.planets()
      future.thenApply({ result -> 
        log.info("[result: $result]")
        if (result != null) {
          routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(result)
        } else {
          routingContext.response()
                        .setStatusCode(404)
                        .end()
        }
      })
    } catch (e: Exception) {
      log.error("[Failed to process]", e)
      routingContext.response()
                    .setStatusCode(500)
                    .putHeader("content-type", "application/json")
                    .end(Json.encode(object {
                      val code    = e::class.java.simpleName
                      val message = e.message
                    }))
    }
  }
}