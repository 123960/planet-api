package api.planet.server.persistence

import java.util.concurrent.*
import com.datastax.driver.core.Cluster
import org.slf4j.LoggerFactory

object PlanetPersistence {

    val log = LoggerFactory.getLogger(PlanetPersistence::class.java)
    val cluster by lazy {
      try {        
        log.info("[Creating database connection][contactpoint: ${System.getProperty("cassandra.contactpoint")}]")
        Cluster.builder()
               .addContactPoint(System.getProperty("cassandra.contactpoint"))
               //.withPoolingOptions(poolingOptions)
               .build()
      } catch (e: Exception) {
        log.info("[Failed creating database][contactpoint: ${System.getProperty("cassandra.contactpoint")}]", e)
        throw e
      }      
    }

    fun postPlanet(id: Int,
                   name: String,
                   climate: String,
                   terrain: String,
                   exhibitions: Int): CompletableFuture<String?> = 
      CompletableFuture.supplyAsync {
        try {
          log.info("[postPlanet][id: $id, name: $name, climate: $climate, terrain: $terrain, exhibitions: $exhibitions]")
          val session = cluster.connect("planet_cli")
          val result  = session.execute("INSERT INTO PLANET_CLI.PLANET (ID, NAME, CLIMATE, EXHIBITIONS, TERRAIN) values ($id, '$name', '$climate', $exhibitions, '$terrain')");
          log.info("[postPlanet][result: $result]")
          "ok"
        } catch (e: Exception) {
          log.error("[postPlanet][id: $id, name: $name, climate: $climate, terrain: $terrain, exhibitions: $exhibitions]", e)
          throw e
        }
      }

    fun deletePlanetById(id: Int): CompletableFuture<String?> = 
      CompletableFuture.supplyAsync {
        try {
          log.info("[deletePlanetById][id: $id]")
          val session = cluster.connect("planet_cli")
          val result  = session.execute("delete from planet where id = $id")
                              .one()
                              ?.toString()
          log.info("[deletePlanetById][result: $result]")
          "ok"
        } catch (e: Exception) {
          log.error("[deletePlanetById][id: $id]", e)
          throw e
        }
      }

    fun getPlanetById(id: Int): CompletableFuture<String?> = 
      CompletableFuture.supplyAsync {
        try {
          log.info("[getPlanetById][id: $id]")
          val session = cluster.connect("planet_cli")
          val result  = session.execute("select json * from planet where id = $id")
                              .one()
                              ?.toString()
          log.info("[getPlanetById][result: $result]")
          result
        } catch (e: Exception) {
          log.error("[getPlanetById][id: $id]", e)
          throw e
        }
      }

    fun getPlanetsByName(name: String, limit: Int): CompletableFuture<List<String>> =
      CompletableFuture.supplyAsync {
        try {
          log.info("[getPlanetsByName][name: $name, limit: $limit]")
          val session = cluster.connect("planet_cli")
          val result  = session.execute("select json * from planet where name = '$name' LIMIT $limit ALLOW FILTERING")
                              .all()
                              .map({row -> row.getString(0)})
          log.info("[getPlanetsByName][result: $result]")
          result
        } catch (e: Exception) {
          log.error("[getPlanetsByName][name: $name]", e)
          throw e
        }
      }

    fun getPlanets(limit: Int): CompletableFuture<List<String>> =
      CompletableFuture.supplyAsync {
        try {
          log.info("[getPlanets][limit: $limit]")
          val session = cluster.connect("planet_cli")
          val result  = session.execute("select json * from planet LIMIT $limit")
                              .all()
                              .map({row -> row.getString(0)})
          log.info("[getPlanets][result: $result]")
          result
        } catch (e: Exception) {
          log.error("[getPlanets][]", e)
          throw e
        }
      }

}