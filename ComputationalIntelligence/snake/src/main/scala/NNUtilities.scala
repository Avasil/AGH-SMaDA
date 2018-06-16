import breeze.linalg.{DenseVector, normalize}
import cats.data.NonEmptyList
import config._

trait NNUtilities {
  val directionVectors = Map(
    DenseVector(-1, 0) -> UP,
    DenseVector(0, 1) -> RIGHT,
    DenseVector(1, 0) -> DOWN,
    DenseVector(0, -1) -> LEFT
  )

  def normalizeVector(vector: DenseVector[Double]): DenseVector[Double] =
    vector / normalize(vector)

  def getAngle(snakeDirection: DenseVector[Double], foodDirection: DenseVector[Double]): Double = {
    val a = normalizeVector(snakeDirection)
    val b = normalizeVector(foodDirection)

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
    DenseVector(vector(1), -vector(0))

  def turnVectorToTheLeft(vector: DenseVector[Double]): DenseVector[Double] =
    DenseVector(-vector(1), vector(0))

  def getFoodDirection(food: DenseVector[Double], snakeHead: DenseVector[Double]): DenseVector[Double] = {
    // e.g. [40, 25] - [25, 25] = [15, 0]
    food - snakeHead
  }

  def getSnakeDirection(snakeHead: DenseVector[Double], snakeAfterHead: DenseVector[Double]): DenseVector[Double] = {
    // it will be different only on one axis
    snakeHead - snakeAfterHead
  }

}
