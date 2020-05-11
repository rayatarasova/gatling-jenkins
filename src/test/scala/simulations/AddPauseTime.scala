package simulations
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

class AddPauseTime extends Simulation{

  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept", "application/json")

  val scn = scenario("Video game DB - 3 calls")
    .exec(http("Get video games - 1st call")
    .get("videogames"))
    .pause(5)

    .exec(http("Get specific video game")
    .get("videogames/1"))
    .pause(1, 20)

    .exec(http("Get all videogames - 2nd call")
    .get("videogames"))
    .pause(3000.milliseconds)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
