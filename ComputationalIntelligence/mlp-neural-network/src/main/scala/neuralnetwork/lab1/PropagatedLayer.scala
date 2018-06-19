package neuralnetwork.lab1

import breeze.linalg.{DenseMatrix, DenseVector}

sealed trait PropagatedLayerSpec {
  val output: DenseVector[Double]
}

final case class PropagatedLayer(
                                  // the input to this layer, values from prev
                                  input: DenseVector[Double],
                                  // the output from this layer, values from curr layer
                                  output: DenseVector[Double],
                                  fDeriv_a: DenseVector[Double], // value of the first derivative of activation function
                                  // the weights for this layer, rows - number of inputs, cols - number of neurons
                                  weights: DenseMatrix[Double],
                                  activationFunc: ActivationSpec
                                ) extends PropagatedLayerSpec {

  def validateResult(label: DenseVector[Double])(p: (DenseVector[Double], DenseVector[Double]) => Boolean): Boolean = {
    p(output, label)
  }
}

object PropagatedLayer {
  private def propagate(propLayer: PropagatedLayerSpec, layer: Layer): PropagatedLayer = {
    val x: DenseVector[Double] = propLayer.output
    val w: DenseMatrix[Double] = layer.weights
    val a: DenseVector[Double] = w * x
    val f: Double => Double = layer.activationFunc.asF
    val y: DenseVector[Double] = a map f
    val fDerivative: Double => Double = layer.activationFunc.asF_deriv
    val fDerivA: DenseVector[Double] = a map fDerivative

    PropagatedLayer(x, y, fDerivA, w, layer.activationFunc)
  }

  def propagateNet(input: DenseVector[Double], net: BackpropNet): List[PropagatedLayer] = {
    val layer0 = PropagatedSensorLayer(input) // validateInput(input, net)
    val calcs = net.layers.scanLeft(layer0: PropagatedLayerSpec)(propagate)

    // HList?
    calcs.tail collect { case x: PropagatedLayer => x }
  }
}

final case class PropagatedSensorLayer(output: DenseVector[Double]) extends PropagatedLayerSpec