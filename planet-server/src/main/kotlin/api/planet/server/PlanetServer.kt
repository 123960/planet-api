package api.planet.server

import io.vertx.core.Future
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.net.SelfSignedCertificate
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import org.slf4j.LoggerFactory

import api.planet.server.handlers.*

class PlanetServer : AbstractVerticle() {

  companion object {
    val log = LoggerFactory.getLogger(PlanetServer::class.java)
  }

  override fun start(startFuture: Future<Void>) {

    OpenAPI3RouterFactory.create(vertx, "src/main/resources/specs/planet.yaml", { spec ->      
      if (spec.succeeded()) {
        log.info("[Reading spec succeed]")
        val routerFactory = spec.result()
        routerFactory.addHandlerByOperationId("get-planet-by-id", GetPlanetById())
        routerFactory.addHandlerByOperationId("get-planet", GetPlanet())
        routerFactory.addHandlerByOperationId("post-planet", PostPlanet())
        routerFactory.addHandlerByOperationId("delete-planet-by-id", DeletePlanetById())
        routerFactory.addHandlerByOperationId("get-swapi", SwapiGetPlanet())

        vertx.createHttpServer()
             .requestHandler(routerFactory.getRouter())
             .listen(System.getProperty("vertx.port").toInt()) { http ->
                if (http.succeeded()) {
                  startFuture.complete()
                  log.info("[Server Started, port: ${System.getProperty("vertx.port")}]")
                } else {
                  startFuture.fail(http.cause())
                }
              }
      } else {
        log.error("[Failed to read spec]", spec.cause())
        throw spec.cause()
      }
    })

  }

}
