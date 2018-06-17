import breeze.linalg.{DenseVector, functions, normalize}
import cats.data.NonEmptyList
import config._

trait NNUtilities {
  val directionVectors = Map(
    DenseVector(-BLOCK_SIZE, 0) -> LEFT,
    DenseVector(0, BLOCK_SIZE) -> DOWN,
    DenseVector(BLOCK_SIZE, 0) -> RIGHT,
    DenseVector(0, -BLOCK_SIZE) -> UP
  )

  def normalizeVector(vector: DenseVector[Double]): DenseVector[Double] = normalize(vector)

  def getAngle(snakeDirection: DenseVector[Double], foodDirection: DenseVector[Double]): Double = {
    val a = normalize(snakeDirection)
    val b = normalize(foodDirection)
    Math.atan2(a(0) * b(1) - a(1) * b(0), a(0) * b(0) + a(1) * b(1)) / Math.PI
  }

  def isDirectionBlocked(snake: NonEmptyList[DenseVector[Double]], snakeDirection: DenseVector[Double]): Boolean = {
    val newCoordinate: DenseVector[Double] = snake.head + snakeDirection

    snake.exists(_ == newCoordinate) ||
      newCoordinate(0) < 0 ||
      newCoordinate(1) < 0 ||
      newCoordinate(0) >= BOARD_WIDTH ||
      newCoordinate(1) >= BOARD_HEIGHT
  }

  def turnVectorToTheRight(vector: DenseVector[Double]): DenseVector[Double] =
    DenseVector(-vector(1), vector(0))

  def turnVectorToTheLeft(vector: DenseVector[Double]): DenseVector[Double] =
    DenseVector(vector(1), -vector(0))

  def getFoodDirection(food: DenseVector[Double], snakeHead: DenseVector[Double]): DenseVector[Double] = {
    food - snakeHead
  }

  def getSnakeDirectionVector(snake: NonEmptyList[DenseVector[Double]]): DenseVector[Double] =
    snake.head - snake.tail.head

  def genCurrentObservation(snake: NonEmptyList[DenseVector[Double]], food: DenseVector[Double]): CurrentObservation = {
    val snakeDirection = getSnakeDirectionVector(snake)
    val foodDirection = getFoodDirection(food, snake.head)
    val barrierLeft = isDirectionBlocked(snake, turnVectorToTheLeft(snakeDirection))
    val barrierFront = isDirectionBlocked(snake, snakeDirection)
    val barrierRight = isDirectionBlocked(snake, turnVectorToTheRight(snakeDirection))
    val angle = getAngle(snakeDirection, foodDirection)

    CurrentObservation(barrierLeft, barrierFront, barrierRight, angle)
  }

  def generateAction(snake: NonEmptyList[DenseVector[Double]]): (Int, Direction) = {
    val rnd = new scala.util.Random
    val randomMove = rnd.nextInt(2) - 1
    (randomMove, getGameAction(snake, randomMove))
  }

  def getGameAction(snake: NonEmptyList[DenseVector[Double]], action: Int): Direction = {
    val snakeDirection = getSnakeDirectionVector(snake)
    directionVectors.getOrElse({
      if (action == -1) {
        turnVectorToTheLeft(snakeDirection).map(_.toInt)
      }
      else if (action == 1) {
        turnVectorToTheRight(snakeDirection).map(_.toInt)
      }
      else {
        snakeDirection.map(_.toInt)
      }
    }, RIGHT)
  }

  def getFoodDistance(head: DenseVector[Double], food: DenseVector[Double]): Double = {
    functions.euclideanDistance(head, food)
  }
}