package neuralnetwork.snake

import breeze.linalg.DenseVector
import cats.data.NonEmptyList

trait SnakeGame {
  def generateFood(snake: NonEmptyList[DenseVector[Double]]): DenseVector[Double] = {
    var correctPos = false
    var newFood: DenseVector[Double] = null
    while (!correctPos) {
      val newX = (Math.random() * (BOARD_WIDTH - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
      val newY = (Math.random() * (BOARD_HEIGHT - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
      if (!snake.exists(_ == DenseVector(newX, newY))) {
        correctPos = true
        newFood = DenseVector(newX, newY)
      }
    }
    newFood
  }

  def describeCurrentState(isRunning: Boolean, score: Int, food: DenseVector[Double], snake: NonEmptyList[DenseVector[Double]]): CurrentState = {
    CurrentState(isRunning, score, snake, food)
  }

  def detectCollision(snake: NonEmptyList[DenseVector[Double]]): Boolean = {
    val newHeadX = snake.head(0)
    val newHeadY = snake.head(1)

    newHeadX < 0 ||
      newHeadX >= BOARD_WIDTH ||
      newHeadY < 0 ||
      newHeadY >= BOARD_HEIGHT ||
      snake.tail.foldLeft(false) { case (detected, node) =>
        detected || (newHeadX == node(0) && newHeadY == node(1))
      }
  }

  def detectFood(head: DenseVector[Double], food: DenseVector[Double]): Boolean =
    head(0) == food(0) && head(1) == food(1)

  def expandSnake(snake: List[DenseVector[Double]], tailX: Double, tailY: Double): List[DenseVector[Double]] = {
    snake :+ DenseVector(tailX, tailY)
  }
}