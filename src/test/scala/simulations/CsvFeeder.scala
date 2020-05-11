package simulations
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeeder extends Simulation{


  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept", "application/json")

  val csvFeeder = csv("data/GameCsvFile.csv").circular

  def getSpecificVideoGame() = {
    repeat(10){
      feed(csvFeeder)
        .exec(http("Get specific video game")
        .get("videogames/${gameId}")
        .check(jsonPath("$.name").is("${gameName}"))
        .check(status.is(200)))
        .pause(1)
    }
  }

  val scn = scenario("Test with feeder")
      .exec(getSpecificVideoGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)


}
