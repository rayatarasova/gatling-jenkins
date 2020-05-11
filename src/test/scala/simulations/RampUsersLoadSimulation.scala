package simulations
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RampUsersLoadSimulation extends Simulation{
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
      //constantUsersPerSec(10) during(10 seconds)
      rampUsersPerSec(1) to (5) during(20 seconds)
    ).protocols(httpConf.inferHtmlResources())
  )

}
