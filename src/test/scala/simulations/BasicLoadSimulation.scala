package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicLoadSimulation extends Simulation{

  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept", "application/json")

  def getAllVideoGames() = {
    exec(
      http("Get all videogames")
        .get("videogames")
          .check(status.is(200))
    )
  }

  def getSpecificVideoGame() = {
    exec(
      http("Get a specific video game")
        .get("videogames/3")
        .check(status.is(200))
    )
  }

  val scn = scenario("Get all videogames")
    .exec(getAllVideoGames())
    .pause(5)
    .exec(getSpecificVideoGame())
    .pause(5)
    .exec(getAllVideoGames())

  setUp(
    scn.inject(
      nothingFor(5 seconds),
      atOnceUsers(5),
    rampUsers(10) during(10 seconds)
    ).protocols(httpConf.inferHtmlResources())
  )

}
