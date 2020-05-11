package simulations
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeederCustom extends Simulation{

  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept", "application/json")

  var idNumbers = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificVideoGame() = {
    repeat(10){
      feed(customFeeder)
        .exec(http("Get specific video game")
          .get("videogames/${gameId}")
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
