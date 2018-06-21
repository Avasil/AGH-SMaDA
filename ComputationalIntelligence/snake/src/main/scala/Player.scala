import cats.implicits._
import javafx.scene.input.KeyCode.{A, D, S, W}
import javafx.scene.input.KeyEvent
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.factory.Nd4j

import scala.collection.immutable

trait Player {
  var currentDirection: Direction

  def chooseDirection(currentState: CurrentState): Direction

  def onKeyPressed(event: KeyEvent): Unit

  def gameFinished(currentState: CurrentState): Boolean

}

object Player {
  def humanPlayer(initialDirection: Direction): Player = new Player with NNUtilities {

    override var currentDirection: Direction = initialDirection

    override def chooseDirection(currentState: CurrentState): Direction = {
      println(genCurrentObservation(currentState.snake, currentState.food))
      println(getFoodDistance(currentState.snake.head, currentState.food))
      currentDirection
    }

    override def onKeyPressed(event: KeyEvent): Unit = {
      event.getCode match {
        case W if currentDirection != DOWN => currentDirection = UP
        case S if currentDirection != UP => currentDirection = DOWN
        case A if currentDirection != RIGHT => currentDirection = LEFT
        case D if currentDirection != LEFT => currentDirection = RIGHT
        case _ =>
      }
    }

    override def gameFinished(currentState: CurrentState): Boolean = {
      currentDirection = initialDirection
      true
    }
  }

  final case class GameResult(steps: Int, score: Int)

  def neuralNetworkPlayer(initialDirection: Direction, model: MultiLayerNetwork): Player = new Player with NNUtilities {

    private var score = 0
    private var steps = 0
    private var game = 1
    private var results: List[GameResult] = List()

    override var currentDirection: Direction = _

    override def chooseDirection(currentState: CurrentState): Direction = {
      score = currentState.score
      steps += 1
      val observation = genCurrentObservation(currentState.snake, currentState.food)
      val input = observation.features

      val predictions: immutable.Seq[(Int, Double)] = for (action <- -1 to 1) yield
        (action, model.output(Nd4j.create(Array(input.data :+ action.toDouble)), false).getDouble(0))


      getGameAction(currentState.snake, predictions.maxBy(_._2)._1)

    }

    override def onKeyPressed(event: KeyEvent): Unit = ()

    override def gameFinished(currentState: CurrentState): Boolean = {
      results ++= List(GameResult(steps, score))
      game += 1
      score = 0
      steps = 0
      currentDirection = initialDirection

      val isFinished = game > 100

      if (isFinished) {
        val avgSteps = results.map(_.steps).sum / results.size
        val avgScore = results.map(_.score).sum / results.size
        val bestScore = results.maxBy(_.score)
        val highestSteps = results.maxBy(_.steps)
        val median = results.map(_.score).sorted.slice(results.size / 2, 1 + results.size / 2).sum / 2

        println(s"Results:\n" +
          s"Games: $game\n" +
          s"Avg Steps: $avgSteps\n" +
          s"Avg Score: $avgScore\n" +
          s"Best Score: ${bestScore.score} achieved with ${bestScore.steps} steps\n" +
          s"Highest Steps: ${highestSteps.steps} achieved with ${highestSteps.score} score\n" +
          s"Median Score: $median")
      }

      !isFinished
    }
  }
}