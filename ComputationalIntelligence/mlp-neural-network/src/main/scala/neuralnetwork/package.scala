import breeze.linalg.DenseVector
import cats.effect.IO

package object neuralnetwork {

  def loadInputs(path: String, delimiter: String): IO[List[DenseVector[Double]]] = IO {
    val bufferedSource = io.Source.fromFile("path")

    val inputs: Iterator[DenseVector[Double]] = for {
      line <- bufferedSource.getLines
    } yield {
      val splitted = line.split(delimiter).map(_.trim).map(_.toDouble)
      DenseVector(splitted)
    }
    bufferedSource.close()

    inputs.toList
  }
}