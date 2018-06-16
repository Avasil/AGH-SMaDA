import breeze.linalg._

class SnakeNeuralNetwork extends NNUtilities {
  def initialPopulation(initialGames: Int) = {
    (0 to initialGames).map { i =>

    }
  }
}

final case class CurrentState(barrierLeft: Boolean, barrierFront: Boolean, barrierRight: Boolean, angle: Double) {
  def features: DenseVector[Double] = {
    val bLeft: Double = if (barrierLeft) 1.0 else 0.0
    val bFront: Double = if (barrierFront) 1.0 else 0.0
    val bRight: Double = if (barrierRight) 1.0 else 0.0

    DenseVector(bLeft, bFront, bRight, angle)
  }
}
