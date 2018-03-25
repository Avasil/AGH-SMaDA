package neuralnetwork

import breeze.linalg.{DenseMatrix, DenseVector}

sealed trait PropagatedLayerSpec {
  val pOut: DenseVector[Double]
}

final case class PropagatedLayer(
                                  pIn: DenseVector[Double], // input to this layer
                                  pOut: DenseVector[Double], // output from this layer
                                  pFDeriv_a: DenseVector[Double], // value of the first derivative of activation function
                                  pw: DenseMatrix[Double], // weights matrix
                                  pAS: ActivationSpec
                                ) extends PropagatedLayerSpec

object PropagatedLayer {
  def propagate(propLayer: PropagatedLayerSpec, layer: Layer): PropagatedLayerSpec = {
    val x: DenseVector[Double] = propLayer.pOut
    val w: DenseMatrix[Double] = layer.lW
    val a: DenseVector[Double] = w * x
    val f: Double => Double = layer.lAS.asF // activation func
    val y: DenseVector[Double] = a map f
    val fDerivative: Double => Double = layer.lAS.asF2
    val fDerivA: DenseVector[Double] = a map fDerivative

    PropagatedLayer(x, y, fDerivA, w, layer.lAS)
  }

  def propagateNet(input: DenseVector[Double], net: BackpropNet): List[PropagatedLayerSpec] = {
    val layer0 = PropagatedSensorLayer(input) // validateInput(input, net)
    val calcs = net.layers.scanLeft(layer0: PropagatedLayerSpec)(propagate)

    calcs.tail
  }
}

final case class PropagatedSensorLayer(pOut: DenseVector[Double]) extends PropagatedLayerSpec