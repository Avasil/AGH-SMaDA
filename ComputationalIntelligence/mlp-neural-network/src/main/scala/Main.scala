import breeze.linalg.DenseVector
import cats.implicits._
import neuralnetwork.{BackpropNet, HyperbolicTangent, Layer, _}

object Main extends App {
  // randomize data or something
  val layers = List(
    // (n, m) = (input, output), Iris ma 4 kolumny
    Layer(initializeWeights(6, 4), HyperbolicTangent()),
    Layer(initializeWeights(5, 6), HyperbolicTangent()),
    Layer(initializeWeights(3, 5), HyperbolicTangent())
  )

  loadInputs("src/main/resources/iris.csv", ",").map { inputs =>
    val initialBpNet = BackpropNet(inputs.head.features, layers = layers, learningRate = 0.015)

    val initialResults: List[PropagatedLayer] = inputs.foldLeft(List[PropagatedLayer]()) { case (acc, InputData(features, _)) =>
      acc :+ PropagatedLayer.propagateNet(features, initialBpNet).last //.validateResult(target)
    }

        val trainedNet = kFoldCrossValidation(5, 500, inputs, initialBpNet).trainedNet
//    val trainedNet = iterate(inputs, initialBpNet, 10)

    val finalResults: List[PropagatedLayer] = inputs.foldLeft(List[PropagatedLayer]()) { case (acc, InputData(features, _)) =>
      //      acc :+ PropagatedLayer.propagateNet(features, trainedNet).last.validateResult(target)
      acc :+ PropagatedLayer.propagateNet(features, trainedNet).last //.validateResult(target)
    }

    val (initialSuccess, finalSuccess) =
      (inputs, initialResults, finalResults)
        .parMapN { case (InputData(_, target), initial, finalNet) =>
//          println(s"Initial Results: ${initial.output} which is ${targetFromDouble(initial.output)} and should be ${targetFromDouble(target)}")
//          println(s"Final Results: ${finalNet.output} which is ${targetFromDouble(finalNet.output)} and should be ${targetFromDouble(target)}")

          (initial.validateResult(target), finalNet.validateResult(target))
        }.foldLeft(List.empty[(Boolean, Boolean)])(_ :+ _)
        .unzip

    val A = initialSuccess.count(_ == true).toDouble / initialSuccess.length
    val B = finalSuccess.count(_ == true).toDouble / finalSuccess.length

    println(s"Success rate:" +
      s"\nInitial: $A" +
      s"\nFinal: $B")

  }.unsafeRunSync()
}