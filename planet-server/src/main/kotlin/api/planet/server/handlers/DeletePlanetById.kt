package api.planet.server.handlers

import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import kotlinx.coroutines.*
import kotlin.system.*

import api.planet.server.persistence.PlanetPersistence

class DeletePlanetById : Handler<RoutingContext> {

  companion object {
    val log = LoggerFactory.getLogger(DeletePlanetById::class.java)
  }

  override fun handle(routingContext: RoutingContext) {
    try {
      var params: RequestParameters = routingContext.get("parsedParameters")
      log.info("[params: ${params.toJson()}]")
      val planetId = params.pathParameter("planetId").getInteger().toInt()
      val future   = PlanetPersistence.getPlanetById(planetId)
      future.thenApply({ result -> 
        if (result != null) {
          val f2 = PlanetPersistence.deletePlanetById(planetId)
          f2.thenApply({ delete -> 
            log.info("[result: $result]")
            if (delete === "ok") {
              routingContext.response()
                            .putHeader("content-type", "application/json")
                            .end(result)
            } else {
              routingContext.response()
                            .setStatusCode(500)
                            .end()
            }
          })
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