package neuralnetwork.snake

import breeze.linalg.DenseVector
import cats.data.NonEmptyList

final case class CurrentObservation(barrierLeft: Boolean, barrierFront: Boolean, barrierRight: Boolean, angle: Double) {
  def features: DenseVector[Double] = {
    val bLeft: Double = if (barrierLeft) 1.0 else 0.0
    val bFront: Double = if (barrierFront) 1.0 else 0.0
    val bRight: Double = if (barrierRight) 1.0 else 0.0

    DenseVector(bLeft, bFront, bRight, angle)
  }
}

final case class CurrentState(isRunning: Boolean, score: Int, snake: NonEmptyList[DenseVector[Double]], food: DenseVector[Double])