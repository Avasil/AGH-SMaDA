package neuralnetwork.lab1

import breeze.linalg._
import breeze.numerics._

final case class Layer(weights: DenseMatrix[Double], // weights matrix
                       activationFunc: ActivationSpec) // activation function

final case class BackpropNet(layers: List[Layer], learningRate: Double)

object BackpropNet {

  def apply(features: DenseVector[Double], layers: List[Layer], learningRate: Double): BackpropNet = {
    layers.map(l => (l.weights.rows, l.weights.cols)).scanLeft((features.length, 0)){
      case ((r1, _), (r2, c2)) => (r2, checkDimensions(r1, c2))
    }

    new BackpropNet(layers, learningRate)
  }
  private def checkDimensions(r1: Int, c2: Int): Int =
    if (r1 == c2) c2
    else throw new Exception("zle wymiary")
}

trait ActivationSpec {
  val asF: Double => Double // activation function
  val asF_deriv: Double => Double // first derivative
}

final case class HyperbolicTangent(asF: Double => Double = tanh(_)) extends ActivationSpec {
  val asF_deriv: Double => Double = x => 1 - Math.pow(tanh(x), 2)
}