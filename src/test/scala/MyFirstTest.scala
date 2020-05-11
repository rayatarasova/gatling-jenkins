import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

class MyFirstTest extends Simulation {

  //http Conf
  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept", "application/json")

  //Scenario
  val scn = scenario("First test")
      .exec(http("Get all games")
        .get("videogames"))

  //Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
