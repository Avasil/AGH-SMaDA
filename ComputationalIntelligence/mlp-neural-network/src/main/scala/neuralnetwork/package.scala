import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.stats.distributions.Rand
import cats.effect.IO

import scala.annotation.tailrec

package object neuralnetwork {

  final case class ValidationResult(successRate: Double)

  final case class InputData(features: DenseVector[Double], target: DenseVector[Double])

  final def loadInputs(path: String, delimiter: String): IO[List[InputData]] =
    IO(io.Source.fromFile(path)).bracket { in =>
      IO {
        val data = for (line <- in.getLines) yield {
          val splitted = line.split(delimiter).map(_.trim)
          val (features, target) = (splitted.dropRight(1).map(_.toDouble), targetToDouble(splitted.last))
          InputData(DenseVector(features), target)
        }
        scala.util.Random.shuffle(data.toList)
      }
    }(in => IO(in.close()))

  private def targetToDouble(s: String): DenseVector[Double] = s match {
    case "Iris-setosa" => DenseVector(1, 0, 0)
    case "Iris-versicolor" => DenseVector(0, 1, 0)
    case "Iris-virginica" => DenseVector(0, 0, 1)
  }

  final def targetFromDouble(s: DenseVector[Double]): String = s.data match {
    case Array(x, y, z) if x > y && x > z => "Iris-setosa"
    case Array(x, y, z) if y > z && y > x => "Iris-versicolor"
    case _ => "Iris-virginica"
  }

  final def isCorrect(v: DenseVector[Double], target: DenseVector[Double]): Boolean = {
    val compareToExpected = (s: String) => s == targetFromDouble(target)

    v.data match {
      case Array(x, y, z) if x > y && x > z => compareToExpected("Iris-setosa")
      case Array(x, y, z) if y > z && y > x => compareToExpected("Iris-versicolor")
      case _ => compareToExpected("Iris-virginica")
    }
  }

  // n = inputs
  // m = neurons
  final def initializeWeights(n: Int, m: Int): DenseMatrix[Double] = {
    DenseMatrix.rand(n, m, Rand.gaussian(0.0, 1.5))
  }

  @tailrec
  final def iterate(inputData: List[InputData], bpNet: BackpropNet, n: Int): BackpropNet = {
    if (n > 0) {
      val backpropagatedNet = inputData.foldLeft(bpNet) { case (backPropNet, InputData(features, target)) =>
        val propagatedNet: List[PropagatedLayer] =
          PropagatedLayer.propagateNet(features, backPropNet)

        BackpropNet(BackpropagatedLayer
          .backpropagateNet(target, propagatedNet)
          .map(BackpropagatedLayer.updateWeights(backPropNet.learningRate, _)), backPropNet.learningRate)
      }

      iterate(inputData, backpropagatedNet, n - 1)
    } else bpNet
  }

  final case class KFoldResult(trainedNet: BackpropNet)

  final def kFoldCrossValidation(k: Int, n: Int, inputs: List[InputData], bpNet: BackpropNet): KFoldResult = {
    val size = inputs.length
    val chunks = size / k

    val parts: List[List[InputData]] = inputs.grouped(chunks).toList

    @tailrec
    def iteration(iter: Int, backPropNet: BackpropNet): KFoldResult = {
      if (iter == k - 1) {
        val learningInputs = parts.slice(0, iter).foldLeft(List.empty[InputData])(_ ++ _)
        val newBpNet = iterate(learningInputs, backPropNet, n)

        KFoldResult(newBpNet)
      } else if (iter == 1) {
        val learningInputs = parts.slice(1, k).foldLeft(List.empty[InputData])(_ ++ _)
        val newBpNet = iterate(learningInputs, backPropNet, n)
        iteration(iter + 1, newBpNet)
      } else {
        val learningInputs = (parts.slice(0, iter) ++ parts.slice(iter + 1, k)).foldLeft(List.empty[InputData])(_ ++ _)
        val newBpNet = iterate(learningInputs, backPropNet, n)
        iteration(iter + 1, newBpNet)
      }
    }

    iteration(0, bpNet)
  }

}