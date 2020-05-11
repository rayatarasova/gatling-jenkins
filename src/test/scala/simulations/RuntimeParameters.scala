package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RunTimeParameters extends Simulation{

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
    .orElse(Option(System.getProperty(propertyName)))
    .getOrElse(defaultValue)
  }

  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt

  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration ${testDuration} seconds")
  }

  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept", "application/json")

  def getAllVideoGames() = {
    exec(
      http("Get all videogames")
        .get("videogames")
        .check(status.is(200))
    )
  }

  val scn = scenario("Get all videogames")
    .forever() {
      exec(getAllVideoGames())
    }


  setUp(
    scn.inject(
      nothingFor(5 seconds),
      rampUsers(userCount) during (rampDuration seconds)
    ).protocols(httpConf)
  ).maxDuration(testDuration seconds)

}
