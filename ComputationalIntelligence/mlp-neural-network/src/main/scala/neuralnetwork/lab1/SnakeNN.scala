package neuralnetwork.lab1

import breeze.linalg.DenseVector
import neuralnetwork.lab1.LoadingData.InputData
import neuralnetwork.lab1.Main.train

object SnakeNN {
  def snakeNet(input: List[InputData]): BackpropNet = {
    val layers = List(
      Layer(initializeWeights(25, 5), HyperbolicTangent()),
      Layer(initializeWeights(1, 25), HyperbolicTangent())
    )
    train(input, layers, 0.01)
  }

  def predict(trainedNet: BackpropNet, input: DenseVector[Double]) = {
    PropagatedLayer.propagateNet(input, trainedNet).last.output
  }
}
