package neuralnetwork

import breeze.linalg.DenseMatrix
import breeze.stats.distributions.Rand

package object lab1 {

  final case class ValidationResult(successRate: Double)

  // n = inputs
  // m = neurons
  final def initializeWeights(n: Int, m: Int): DenseMatrix[Double] = {
    DenseMatrix.rand(n, m, Rand.gaussian(0.0, 1.5))
  }
}