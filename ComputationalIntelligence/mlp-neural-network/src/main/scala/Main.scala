import breeze.linalg.DenseVector
import cats.implicits._
import neuralnetwork.{BackpropNet, HyperbolicTangent, Layer, _}

object Main extends App {

  val layers = List(
    // (n, m) = (input, output), Iris ma 4 kolumny
    Layer(initializeWeights(6, 4), HyperbolicTangent()),
    Layer(initializeWeights(3, 6), HyperbolicTangent())
  )

  loadInputs("src/main/resources/iris.csv", ",").map { inputs =>
    // same wymiary sprawdzam
    val backpropagateNet = BackpropNet(inputs.head._1, layers = layers, learningRate = 1.25)

    var successRateBefore: List[Boolean] = List()
    var successRateAfter: List[Boolean] = List()

    var firstResults: List[DenseVector[Double]] = List()
    var secondResults: List[DenseVector[Double]] = List()

    val firstBackProp: List[Layer] = inputs.foldLeft(List[Layer]()) { case (_, (features, target)) =>

      val propagatedNet: List[PropagatedLayer] =
        PropagatedLayer.propagateNet(features, backpropagateNet)

      firstResults :+= propagatedNet.last.output

      BackpropagatedLayer
        .backpropagateNet(target, propagatedNet)
        .map { layer =>
          BackpropagatedLayer.updateWeights(backpropagateNet.learningRate, layer)
        }
    }

    inputs.foreach { case (features, _) =>
      val updatedPropagatedLayers = PropagatedLayer.propagateNet(features, BackpropNet(firstBackProp, backpropagateNet.learningRate))
      secondResults :+= updatedPropagatedLayers.last.output
    }

    (inputs, firstResults, secondResults).parMapN { case ((_, target), first, second) =>
      println(s"firstResult: $first which is ${validateOutput(first, target)}")
      println(s"secondResult: $second which is ${validateOutput(second, target)}")

      successRateBefore :+= judge(first, target)
      successRateAfter :+= judge(second, target)
    }

    println(successRateBefore)
    println(successRateAfter)
    val A = successRateBefore.count(_ == true).toDouble / successRateBefore.length
    val B = successRateAfter.count(_ == true).toDouble / successRateAfter.length

    println(s"Success rate:" +
      s"\nBefore: $A" +
      s"\nAfter: $B")
  }.unsafeRunSync()

}