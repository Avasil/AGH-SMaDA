package neuralnetwork

import breeze.linalg._
import breeze.numerics._

final case class Layer(lW: DenseMatrix[Double], // weights matrix
                       lAS: ActivationSpec) // activation function

final case class BackpropNet(layers: List[Layer], learningRate: Double)

object BackpropNet {
  private def checkDimensions(m1: DenseMatrix[Double], m2: DenseMatrix[Double]): DenseMatrix[Double] =
    if (m1.rows == m2.cols) m2
    else throw new Exception("zle wymiary")
}

trait ActivationSpec {
  val asF: Double => Double // activation function
  val asF2: Double => Double // first derivative
}

final case class HyperbolicTangent(asF: Double => Double = tanh(_)) extends ActivationSpec {
  val asF2: Double => Double = x => 1 - Math.pow(tanh(x), 2)
}