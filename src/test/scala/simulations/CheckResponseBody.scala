package simulations
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

class CheckResponseBody extends Simulation{

  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept", "application/json")

  val scn = scenario("Check Json path")
    .exec(http("Get specific video game")
      .get("videogames/1")
      .check(status.in(200 to 210), jsonPath("$.name").is("Resident Evil 4")))
    .pause(1, 20)

    .exec(http("Get all video game")
      .get("videogames")
      .check(jsonPath("$[1].id").saveAs("gameId")))
    .exec{ session => println(session); session }

    .exec(http("Get specific game")
    .get("videogames/${gameId}")
    .check(jsonPath("$.name").is("Gran Turismo 3"))
    .check(bodyString.saveAs("responseBody")))
    .exec{ session => println(session("responseBody").as[String]); session }


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}