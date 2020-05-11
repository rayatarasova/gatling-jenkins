package simulations
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

class CheckResponseCode extends Simulation{

  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept", "application/json")

  val scn = scenario("Video game DB - 3 calls")
    .exec(http("Get video games - 1st call")
      .get("videogames")
        .check(status.is(200)))
    .pause(5)

    .exec(http("Get specific video game")
      .get("videogames/1")
        .check(status.in(200 to 210)))
    .pause(1, 20)

    .exec(http("Get all videogames - 2nd call")
      .get("videogames")
        .check(status.not(400), status.not(500)))
    .pause(3000.milliseconds)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}