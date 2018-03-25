package neuralnetwork

import breeze.linalg.{DenseMatrix, DenseVector}

final case class BackpropagatedLayer(
                                      bpDazzle: DenseVector[Double], // wtf
                                      bpErrGrad: DenseVector[Double], // the error due to this layer
                                      bpFDerA: DenseVector[Double], // value of the first derivative of the activation func
                                      bpIn: DenseVector[Double], // the input to this layer
                                      bpOut: DenseVector[Double], // the output from this layer
                                      bpW: DenseMatrix[Double], // the weights for this layer
                                      bpAS: ActivationSpec
                                    )

object BackpropagatedLayer {
  def backpropagate(propLayer: PropagatedLayer, backpropLayer: BackpropagatedLayer): BackpropagatedLayer = {
    val fDeriv_aK: DenseVector[Double] = backpropLayer.bpFDerA
    val fDeriv_aJ: DenseVector[Double] = propLayer.pFDeriv_a

    val wKT = backpropLayer.bpW.t
    val dazzleBP = backpropLayer.bpDazzle
    val dazzleP = wKT * (dazzleBP *:* fDeriv_aK)

    BackpropagatedLayer(
      dazzleP,
      errorGrad(dazzleP, fDeriv_aJ, propLayer.pIn).toDenseVector, // ???
      propLayer.pFDeriv_a,
      propLayer.pIn,
      propLayer.pOut,
      propLayer.pw,
      propLayer.pAS
    )
  }

  def backpropagateFinalLayer(propLayer: PropagatedLayer, t: DenseVector[Double]): BackpropagatedLayer = {
    val dazzle = propLayer.pOut - t
    val fDeriv_a = propLayer.pFDeriv_a

    BackpropagatedLayer(
      dazzle,
      errorGrad(dazzle, fDeriv_a, propLayer.pIn).toDenseVector, // ???
      propLayer.pFDeriv_a,
      propLayer.pIn,
      propLayer.pOut,
      propLayer.pw,
      propLayer.pAS
    )
  }

  def errorGrad(dazzle: DenseVector[Double], fDeriv_a: DenseVector[Double], input: DenseVector[Double]): DenseMatrix[Double] = {
    (dazzle *:* fDeriv_a) * input.t
  }

  def backpropagateNet(target: DenseVector[Double], layers: List[PropagatedLayer]): List[BackpropagatedLayer] = {
    val hiddenLayers = layers.dropRight(1)
    val layerL = backpropagateFinalLayer(layers.last, target)

    hiddenLayers.scanRight(layerL)(backpropagate)
  }
}