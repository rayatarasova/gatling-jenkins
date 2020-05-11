package simulations

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class FinalScenario extends Simulation{

  private def getProperty(propertyName: String, defaultValue: String): String = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt

  var idNumbers = (7001 to 7100).iterator
  val rnd = new Random()
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val now = LocalDate.now()

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate, random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString(5)),
    "releaseDate" -> getRandomDate(now, rnd),
    "reviewScore" -> rnd.nextInt(100),
    "category" -> ("Category-" + randomString(6)),
    "rating" -> ("Rating-" + randomString(4))
  ))


  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration ${testDuration} seconds")
  }

  val httpConf = http.baseUrl("http://video-game-db.eu-west-2.elasticbeanstalk.com/app/")
    .header("Accept","application/json")

  def getAllVideoGames() = {
    println("Get all video games")
    exec(
      http("Get all video games")
      .get("videogames")
      .check(status.is(200))
    )
  }

  def getVideoGameById() = {
    exec(
      http("Get a specific video games")
        .get("videogames/${gameId}")
        .check(status.is(200))
        .check(jsonPath("$.name").is("${name}"))
    )
  }

  def createNewVideoGame() = {
    println(s"Create new video game with body: ")
    feed(customFeeder)
    .exec(
      http("Create new video game")
        .post("videogames/")
        .body(ElFileBody("bodies/NewGameTemplate.json")).asJson
        .check(status.is(200))
    )
  }

  def deleteVideoGame() = {
    exec(
      http("Delete video game")
        .delete("videogames/${gameId}")
        .check(status.is(200))
    )
  }

  val scn = scenario("My full test scenario")
    .forever() {
      exec(getAllVideoGames())
        .pause(2)
        .exec(createNewVideoGame())
        .pause(2)
        .exec(getVideoGameById())
        .pause(2)
        .exec(deleteVideoGame())
    }


  setUp(
    scn.inject(
      nothingFor(2),
      rampUsers(userCount) during (rampDuration seconds)
    ).protocols(httpConf)
  ).maxDuration(testDuration seconds)

  after {
    println("End of test")
  }
}
