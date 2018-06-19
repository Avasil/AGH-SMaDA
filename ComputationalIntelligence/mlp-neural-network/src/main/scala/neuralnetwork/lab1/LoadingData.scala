package neuralnetwork.lab1

import breeze.linalg.DenseVector
import cats.effect.IO

object LoadingData {

  final case class InputData(features: DenseVector[Double], target: DenseVector[Double])

  final def loadInputs(implicit F: TrainingData): IO[List[InputData]] =
    IO(io.Source.fromFile(F.path)).bracket { in =>
      IO {
        val data = for (line <- in.getLines) yield {
          val splitted = line.split(F.delim).map(_.trim)
          val (features, label) = (splitted.dropRight(1).map(_.toDouble), F.labelToDouble(splitted.last))
          InputData(DenseVector(features), label)
        }
        scala.util.Random.shuffle(data.toList)
      }
    }(in => IO(in.close()))
}
