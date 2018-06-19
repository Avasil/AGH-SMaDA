package neuralnetwork.snake

import breeze.linalg._
import cats.data.NonEmptyList

import scala.collection.mutable

class SnakeTrainingFacility extends SnakeGame with NNUtilities {
  var isRunning = false
  var score = 0
  val learningRate = 0.01

  def start(): CurrentState = {
    val genX = (Math.random() * (BOARD_WIDTH - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
    val genY = (Math.random() * (BOARD_HEIGHT - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE

    val snake: NonEmptyList[DenseVector[Double]] = NonEmptyList(DenseVector(genX, genY), List(DenseVector(genX + BLOCK_SIZE, genY)))
    val food = generateFood(snake)
    isRunning = true
    score = 0

    describeCurrentState(isRunning, score, food, snake)
  }

  def step(snake: NonEmptyList[DenseVector[Double]], direction: Direction, food: DenseVector[Double]): CurrentState = {
    // need to remove tail
    val oldTail: DenseVector[Double] = snake.tail.last
    val newSnake = moveSnake(snake, direction)
    val collisionCheck = detectCollision(newSnake)

    if (collisionCheck) {
      isRunning = false
      describeCurrentState(isRunning, score, food, newSnake)
    } else if (detectFood(newSnake.head, food)) {
      val expandedSnake = expandSnake(newSnake.tail, oldTail(0), oldTail(1))
      score += 1
      val updatedSnake = NonEmptyList(newSnake.head, expandedSnake)
      val newFood = generateFood(updatedSnake)

      describeCurrentState(isRunning, score, newFood, updatedSnake)
    } else {
      describeCurrentState(isRunning, score, food, newSnake)
    }
  }

  def moveSnake(snake: NonEmptyList[DenseVector[Double]], direction: Direction): NonEmptyList[DenseVector[Double]] = {
    val oldHeadX = snake.head(0)
    val oldHeadY = snake.head(1)
    val newHead = direction match {
      case UP =>
        DenseVector(oldHeadX, oldHeadY - BLOCK_SIZE)
      case DOWN =>
        DenseVector(oldHeadX, oldHeadY + BLOCK_SIZE)
      case LEFT =>
        DenseVector(oldHeadX - BLOCK_SIZE, oldHeadY)
      case RIGHT =>
        DenseVector(oldHeadX + BLOCK_SIZE, oldHeadY)
    }
    NonEmptyList(newHead, List(snake.head) ++ snake.tail.dropRight(1))
  }

  def initialPopulation(initialGames: Int): List[TrainingResult] = {
    val trainingData = mutable.Buffer[TrainingResult]()
    (0 to initialGames).foreach { _ =>
      val CurrentState(_, prevScore, snake, food) = start()
      var currentSnake = snake
      var previousObservation: CurrentObservation = genCurrentObservation(currentSnake, food)
      var previousFoodDistance: Double = getFoodDistance(food, currentSnake.head)
      var maxSteps = 1000
      while (isRunning && maxSteps > 0) {
        val (action, newDirection) =
        //          if (previousObservation.angle == 0.0) (0, getGameAction(currentSnake, 0))
        //          else if(previousObservation.angle < 0.0) (-1, getGameAction(currentSnake, -1))
        //          else (1, getGameAction(currentSnake, 1))
          generateAction(currentSnake)
        val CurrentState(running, newScore, newSnake, newFood) = step(currentSnake, newDirection, food)
        currentSnake = newSnake
        if (!running) {
          trainingData.append(TrainingResult(previousObservation, action, -25))
        } else {
          val newFoodDistance = getFoodDistance(newFood, currentSnake.head)
          if (newScore > prevScore) {
            trainingData.append(TrainingResult(previousObservation, action, 3))
          } else if (newFoodDistance < previousFoodDistance) {
            trainingData.append(TrainingResult(previousObservation, action, 1))
          } else {
            trainingData.append(TrainingResult(previousObservation, action, 0))
          }
          previousObservation = genCurrentObservation(currentSnake, newFood)
          previousFoodDistance = newFoodDistance
        }

        maxSteps -= 1
      }
    }
    trainingData.toList
  }

}

final case class TrainingResult(previousObservation: CurrentObservation, action: Int, reward: Int)