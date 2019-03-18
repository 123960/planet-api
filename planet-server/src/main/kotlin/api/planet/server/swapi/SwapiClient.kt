package api.planet.server.swapi

import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.core.http.HttpMethod.*
import java.util.concurrent.*
import java.util.stream.Collectors
import org.slf4j.LoggerFactory

object SwapiClient {

  val log = LoggerFactory.getLogger(SwapiClient::class.java)
  val client by lazy {
    WebClient.create(Vertx.vertx())
  }

  fun planets(): CompletableFuture<String> {
    log.info("[Getting all planets][]");
    val future = CompletableFuture<String>()    
    client.requestAbs(GET, "http://www.swapi.co/api/planets").send({ ar ->
      if (ar.succeeded()) {
        var response = ar.result()
        log.info("[Getting all planets][statusCode: ${response.statusCode()}, response: ${response.body().toString("ISO-8859-1")}]")
        if (response.statusCode() == 200) {
          val result = response.bodyAsJsonObject()
                               .getJsonArray("results")
                               .stream()
                               .map({ obj ->
                                  val json = obj as JsonObject
                                  Json.encode(object {
                                    val id          = json.getInteger("id")?.toInt()
                                    val name        = json.getString("name")
                                    val climate     = json.getString("climate")
                                    val terrain     = json.getString("terrain")
                                    val exhibitions = json.getJsonArray("films").size().toInt()
                                  })
                                })
          future.complete(result.collect(Collectors.toList()).joinToString(prefix = "[", postfix = "]"))
        } else {
          future.complete("[]")
        }
      } else {
        log.error("[Getting all planets]", ar.cause())
        future.completeExceptionally(ar.cause())
      }
    })
    return future
  }

  fun planetExhibitions(id: Int): CompletableFuture<Int> {
    log.info("[Getting exhibition info][id: $id]");
    val future = CompletableFuture<Int>()    
    client.requestAbs(GET, "http://www.swapi.co/api/planets/$id").send({ ar ->
      if (ar.succeeded()) {
        var response = ar.result()
        log.info("[Getting exhibition info][statusCode: ${response.statusCode()}, response: ${response.body().toString("ISO-8859-1")}]")
        if (response.statusCode() == 200) {
          future.complete(response.bodyAsJsonObject().getJsonArray("films").size().toInt())
        } else {
          future.complete(0)
        }
      } else {
        log.error("[Getting exhibition info][id: $id]", ar.cause())
        future.completeExceptionally(ar.cause())
      }
    })
    return future
  }
    
      

}