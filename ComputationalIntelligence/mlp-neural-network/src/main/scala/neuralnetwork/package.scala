import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.stats.distributions.Rand
import cats.effect.IO

package object neuralnetwork {

  def loadInputs(path: String, delimiter: String): IO[List[(DenseVector[Double], DenseVector[Double])]] = IO {
    val bufferedSource = io.Source.fromFile(path)

    val inputs: List[(DenseVector[Double], DenseVector[Double])] = (for {
      line <- bufferedSource.getLines
    } yield {
      val splitted = line.split(delimiter).map(_.trim)
      val (features, target) = (splitted.dropRight(1).map(_.toDouble), targetToDouble(splitted.last))
      (DenseVector(features), target)
    }).toList
    bufferedSource.close()

    inputs
  }

  private def targetToDouble(s: String): DenseVector[Double] = s match {
    case "Iris-setosa" => DenseVector(1, 0, 0)
    case "Iris-versicolor" => DenseVector(0, 1, 0)
    case "Iris-virginica" => DenseVector(0, 0, 1)
  }

  private def targetFromDouble(s: DenseVector[Double]): String = s.data match {
    case Array(1, 0, 0) => "Iris-setosa"
    case Array(0, 1, 0) => "Iris-versicolor"
    case Array(0, 0, 1) => "Iris-virginica"
  }

  def validateOutput(v: DenseVector[Double], target: DenseVector[Double]): String = {
    val result = s" <=> ${targetFromDouble(target)}"
    val dobrzeNiedobrze = (s: String) => " WHICH IS..." + (if (s == targetFromDouble(target)) "DOBRZE !!!! :O" else "NIEDOBRZE :(")
    v.data match {
      case Array(x, y, z) if x > y && x > z => "Iris-setosa" + result + dobrzeNiedobrze("Iris-setosa")
      case Array(x, y, z) if y > z && y > x => "Iris-versicolor" + result + dobrzeNiedobrze("Iris-versicolor")
      case _ => "Iris-virginica" + result + dobrzeNiedobrze("Iris-virginica")
    }
  }

  def judge(v: DenseVector[Double], target: DenseVector[Double]): Boolean = {
    val dobrzeNiedobrze = (s: String) => s == targetFromDouble(target)

    v.data match {
      case Array(x, y, z) if x > y && x > z => dobrzeNiedobrze("Iris-setosa")
      case Array(x, y, z) if y > z && y > x => dobrzeNiedobrze("Iris-versicolor")
      case _ => dobrzeNiedobrze("Iris-virginica")
    }
  }

  // n = inputs
  // m = neurons
  def initializeWeights(n: Int, m: Int): DenseMatrix[Double] = {
    DenseMatrix.rand(n, m, Rand.gaussian(0.0, 1.5))
  }
}