package neuralnetwork.lab1

import neuralnetwork.lab1.LoadingData.loadInputs

object Main extends App with NNUtilities {
  val layers = List(
    // (n, m) = (input, output), Iris ma 4 kolumny
    // (output, input)
    Layer(initializeWeights(6, 4), HyperbolicTangent()),
    Layer(initializeWeights(5, 6), HyperbolicTangent()),
    Layer(initializeWeights(3, 5), HyperbolicTangent())
  )

  val secondLayers = List(
    Layer(initializeWeights(13, 7), HyperbolicTangent()),
    Layer(initializeWeights(9, 13), HyperbolicTangent()),
    Layer(initializeWeights(3, 9), HyperbolicTangent()))

  implicit val trainingData = TrainingData.irisData

  val result =
    for (inputs <- loadInputs) yield {
      val trainedNet = train(inputs, layers, 0.01)
      val (newInputs, trainedMergedNet) = merge(inputs, trainedNet, secondLayers, 0.01)

      val successRatio1 = test(inputs, trainedNet)
      val successRatio2 = test(newInputs, trainedMergedNet)

      s"Success ratio (first net): $successRatio1\n" +
        s"Success ratio (second net): $successRatio2"
    }

  result
    .map(println)
    .unsafeRunSync()
}