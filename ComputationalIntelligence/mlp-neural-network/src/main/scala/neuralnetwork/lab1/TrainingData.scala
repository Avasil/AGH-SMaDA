package neuralnetwork.lab1

import breeze.linalg.DenseVector

trait TrainingData {
  def path: String

  def delim: String

  def isCorrect(result: DenseVector[Double], label: DenseVector[Double]): Boolean

  def labelToDouble(s: String): DenseVector[Double]

  def labelFroMDouble(s: DenseVector[Double]): String
}

object TrainingData {
  val irisData = new TrainingData {
    override def labelToDouble(s: String): DenseVector[Double] = s match {
      case "Iris-setosa" => DenseVector(1, 0, 0)
      case "Iris-versicolor" => DenseVector(0, 1, 0)
      case "Iris-virginica" => DenseVector(0, 0, 1)
    }

    override def path: String = "src/main/resources/iris.csv"

    override def labelFroMDouble(s: DenseVector[Double]): String = s.data match {
      case Array(x, y, z) if x > y && x > z => "Iris-setosa"
      case Array(x, y, z) if y > z && y > x => "Iris-versicolor"
      case _ => "Iris-virginica"
    }

    override def delim: String = ","

    override def isCorrect(result: DenseVector[Double], label: DenseVector[Double]): Boolean = {
      val compareToExpected = (s: String) => s == labelFroMDouble(label)

      result.data match {
        case Array(x, y, z) if x > y && x > z => compareToExpected("Iris-setosa")
        case Array(x, y, z) if y > z && y > x => compareToExpected("Iris-versicolor")
        case _ => compareToExpected("Iris-virginica")
      }
    }
  }

  val wineData = new TrainingData {
    override def labelToDouble(s: String): DenseVector[Double] = ???

    override def labelFroMDouble(s: DenseVector[Double]): String = ???

    override def path: String = ???

    override def delim: String = ???

    override def isCorrect(result: DenseVector[Double], label: DenseVector[Double]): Boolean = ???
  }
}