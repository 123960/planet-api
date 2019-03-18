package api.planet.server.handlers

import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

import api.planet.server.persistence.PlanetPersistence

class GetPlanet : Handler<RoutingContext> {

  companion object {
    val log = LoggerFactory.getLogger(GetPlanet::class.java)
  } 

  override fun handle(routingContext: RoutingContext) {
    try {
      var params: RequestParameters = routingContext.get("parsedParameters")
      log.info("[params: ${params.toJson()}]")
      val name    = params.queryParameter("name")?.getString()
      val limit   = params.queryParameter("limit")?.getInteger()?.toInt() ?: System.getProperty("rest.get.limit").toInt()
      val future  = if (name != null) {
                      PlanetPersistence.getPlanetsByName(name, limit)
                    } else {
                      PlanetPersistence.getPlanets(limit)
                    }
      future.thenApply({ result -> 
        log.info("[result: $result]")
        if (result != null) {
          routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(result.joinToString(prefix = "[", postfix = "]"))
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