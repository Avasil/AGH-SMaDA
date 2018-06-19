package neuralnetwork.lab1

import breeze.linalg.{DenseMatrix, DenseVector, _}

final case class BackpropagatedLayer(
                                      bpDazzle: DenseVector[Double], // wtf
                                      bpErrGrad: DenseMatrix[Double], // the error due to this layer
                                      fDeriv_a: DenseVector[Double], // value of the first derivative of the activation func
                                      // the input to this layer, values from prev
                                      layerinput: DenseVector[Double],
                                      // the output from this layer, values from curr layer
                                      output: DenseVector[Double],
                                      // the weights for this layer, rows - number of inputs, cols - number of neurons
                                      weights: DenseMatrix[Double],
                                      activationFunc: ActivationSpec
                                    )

object BackpropagatedLayer {
  private def backpropagate(propLayer: PropagatedLayer, backpropLayer: BackpropagatedLayer): BackpropagatedLayer = {
    val fDeriv_aK: DenseVector[Double] = backpropLayer.fDeriv_a
    val fDeriv_aJ: DenseVector[Double] = propLayer.fDeriv_a

    val wKT = backpropLayer.weights.t
    val dazzleBP = backpropLayer.bpDazzle
    val dazzleP = wKT * (dazzleBP *:* fDeriv_aK)

    BackpropagatedLayer(
      dazzleP,
      errorGrad(dazzleP, fDeriv_aJ, propLayer.input), // ???
      propLayer.fDeriv_a,
      propLayer.input,
      propLayer.output,
      propLayer.weights,
      propLayer.activationFunc
    )
  }

  private def backpropagateFinalLayer(propLayer: PropagatedLayer, t: DenseVector[Double]): BackpropagatedLayer = {
    val dazzle = propLayer.output - t
    val fDeriv_a = propLayer.fDeriv_a

    BackpropagatedLayer(
      dazzle,
      errorGrad(dazzle, fDeriv_a, propLayer.input), // ???
      propLayer.fDeriv_a,
      propLayer.input,
      propLayer.output,
      propLayer.weights,
      propLayer.activationFunc
    )
  }

  private def errorGrad(dazzle: DenseVector[Double], fDeriv_a: DenseVector[Double], input: DenseVector[Double]): DenseMatrix[Double] = {
    (dazzle *:* fDeriv_a) * input.t
  }

  def backpropagateNet(target: DenseVector[Double], layers: List[PropagatedLayer]): List[BackpropagatedLayer] = {
    val hiddenLayers = layers.dropRight(1)
    val layerL = backpropagateFinalLayer(layers.last, target)

    hiddenLayers.scanRight(layerL)(backpropagate)
  }

  def updateWeights(learningRate: Double, backpropagatedLayer: BackpropagatedLayer): Layer = {
    val wOld: DenseMatrix[Double] = backpropagatedLayer.weights
    val delW = learningRate *:* backpropagatedLayer.bpErrGrad
    val wNew = wOld -:- delW

    Layer(wNew, backpropagatedLayer.activationFunc)
  }
}