package api.planet.server.handlers

import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.UUID

import api.planet.server.swapi.SwapiClient
import api.planet.server.persistence.PlanetPersistence

class PostPlanet : Handler<RoutingContext> {

  companion object {
    val log = LoggerFactory.getLogger(PostPlanet::class.java)
  }

  override fun handle(routingContext: RoutingContext) {
    try {
      var params: RequestParameters = routingContext.get("parsedParameters")
      val body = routingContext.getBodyAsJson()
      log.info("[params: ${params.toJson()}, body: ${body}]")
      val id     = body.getInteger("id").toInt()
      val f1     = SwapiClient.planetExhibitions(id)
      f1.thenApply({ exhibitions -> 
        val f2     = PlanetPersistence.postPlanet(id          = id,
                                                  name        = body.getString("name"),
                                                  climate     = body.getString("climate"),
                                                  terrain     = body.getString("terrain"),
                                                  exhibitions = exhibitions)
        f2.thenApply({ result -> 
          log.info("[result: $result]")
          if (result === "ok") {
            routingContext.response()
                          .putHeader("content-type", "application/json")
                          .end(Json.encode(object {
                            val id          = id
                            val name        = body.getString("name")
                            val climate     = body.getString("climate")
                            val terrain     = body.getString("terrain")
                            val exhibitions = exhibitions
                          }))
          } else {
            routingContext.response()
                          .setStatusCode(500)
                          .end()
          }
        })   
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