package neuralnetwork.lab1

import breeze.linalg.DenseVector
import cats.implicits._
import neuralnetwork.lab1.LoadingData.InputData

import scala.annotation.tailrec
import scala.util.Random

trait NNUtilities {
  def train(inputs: List[InputData], layers: List[Layer], learningRate: Double): BackpropNet = {
    val initialBpNet = BackpropNet(inputs.head.features, layers = layers, learningRate = learningRate)
    kFoldCrossValidation(10, 20, inputs, initialBpNet).trainedNet
  }

  def test(inputs: List[InputData], net: BackpropNet)(implicit F: TrainingData): Double = {
    val finalResults: List[PropagatedLayer] = inputs.foldLeft(List[PropagatedLayer]()) { case (acc, InputData(features, _)) =>
      acc :+ PropagatedLayer.propagateNet(features, net).last
    }

    val finalSuccess = (inputs, finalResults)
      .parMapN { case (InputData(_, label), finalNet) => finalNet.validateResult(label)(F.isCorrect) }
      .foldLeft(List.empty[Boolean])(_ :+ _)

    val successRatio = finalSuccess.count(_ == true).toDouble / finalSuccess.length

    successRatio
  }

  def merge(inputs: List[InputData], first: BackpropNet, secondLayers: List[Layer], learningRate: Double): (List[InputData], BackpropNet) = {
    val firstResults: List[PropagatedLayer] = inputs.foldLeft(List[PropagatedLayer]()) { case (acc, InputData(features, _)) =>
      acc :+ PropagatedLayer.propagateNet(features, first).last
    }

    val newInputs = (inputs, firstResults)
      .parMapN { case (InputData(features, target), PropagatedLayer(_, output, _, _, _)) =>
        InputData(DenseVector(features.data ++ output.data), target)
      }

    (newInputs, train(newInputs, secondLayers, learningRate))
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

  final def kFoldCrossValidation(k: Int, iters: Int, inputs: List[InputData], bpNet: BackpropNet): KFoldResult = {
    val size = inputs.length
    val chunks = size / k

    val parts: List[List[InputData]] = inputs.grouped(chunks).toList

    @tailrec
    def iteration(iter: Int, backPropNet: BackpropNet): KFoldResult = {
      if (iter == k - 1) {
        val learningInputs = parts.slice(0, iter).foldLeft(List.empty[InputData])(_ ++ _)
        val newBpNet = iterate(learningInputs, backPropNet, iters)

        KFoldResult(newBpNet)
      } else if (iter == 1) {
        val learningInputs = parts.slice(1, k).foldLeft(List.empty[InputData])(_ ++ _)
        val newBpNet = iterate(learningInputs, backPropNet, iters)
        iteration(iter + 1, newBpNet)
      } else {
        val learningInputs = (parts.slice(0, iter) ++ parts.slice(iter + 1, k)).foldLeft(List.empty[InputData])(_ ++ _)
        val newBpNet = iterate(learningInputs, backPropNet, iters)
        iteration(iter + 1, newBpNet)
      }
    }

    iteration(0, bpNet)
  }

  def genLearningRate(): Double = {
    val r = new Random(System.nanoTime() + 14441)
    val rangeMin = 0.001
    val rangeMax = 0.02
    rangeMin + (rangeMax - rangeMin) * r.nextDouble
  }
}
