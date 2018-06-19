package neuralnetwork.lab3

import breeze.linalg.{max, min}
import breeze.numerics.abs
import cats.effect.IO
import cats.implicits._

import scala.collection.mutable
import scala.math.BigDecimal.RoundingMode

object AGDS extends App {
  val ioData = loadData("src/main/resources/iris.csv")

  ioData.map { case LoadedData(irises, params) =>

    val lookingFor = irises(11).copy(weight = 1.0)
    val newIrises = irises.map(i => if (i.id == lookingFor.id) i.copy(weight = 1.0) else i)
    val weights = calculateWeights(params, lookingFor)
    val similar: List[(Int, Double)] = getSimilarities(weights, newIrises).sortBy(-_._2)

    classify(irises(33), irises, params)
    mostSimilar(lookingFor, similar, newIrises)
  }.unsafeRunSync()

  def mostSimilar(iris: IrisAGDS, similarities: List[(Int, Double)], irises: List[IrisAGDS]): Unit = {
    val mostSimilar = similarities.sortBy(-_._2).take(6).map { case (id, sim) => irises(id).show + s" with similarity = $sim %" }

    println(s"\n${iris.show} is most similar to: ")
    mostSimilar.drop(1).foreach(println)
  }

  def classify(iris: IrisAGDS, irises: List[IrisAGDS], params: Map[String, Map[Double, List[IrisAGDS]]]) = {
    val correctClass = iris.className
    val lookingFor = iris.copy(id = -1337, weight = 1.0, className = "UNKOWN")
    val newIrises = irises.map(i => if (i.id == iris.id) lookingFor else i)
    val weights = calculateWeights(params, lookingFor)
    val similar: List[(Int, Double)] = getSimilarities(weights, newIrises).sortBy(-_._2)

    val mostSimilar = similar.sortBy(-_._2).slice(1, 6)
    val mostSimilarString = mostSimilar.map { case (id, sim) => newIrises(id).show + s" with similarity = $sim %" }

    println(s"\n${lookingFor.show} is classified as ${newIrises(mostSimilar.head._1).className} (expected: $correctClass) due to similarity with:")
    mostSimilarString.foreach(println)
  }

  def irisMap(s: String): Int = s match {
    case "Iris-setosa" => 1
    case "Iris-versicolor" => 2
    case "Iris-virginica" => 3
    case _ => -1
  }

  final case class LoadedData(irises: List[IrisAGDS], params: Map[String, Map[Double, List[IrisAGDS]]])

  def loadData(path: String): IO[LoadedData] = {
    IO(io.Source.fromFile(path)).bracket { in =>
      IO {
        var id = 1
        val data = for (line <- in.getLines) yield {
          val splitted = line.split(",").map(_.trim)
          val sepalLength = splitted(0).toDouble
          val sepalWidth = splitted(1).toDouble
          val petalLength = splitted(2).toDouble
          val petalWidth = splitted(3).toDouble
          val `class` = splitted.last

          val iris = IrisAGDS(id, `class`, petalLength, sepalLength, petalWidth, sepalWidth)
          id += 1

          (iris,
            Map(
              "class" -> Map(irisMap(`class`).toDouble -> List(iris)),
              "sepalLength" -> Map(sepalLength -> List(iris)),
              "sepalWidth" -> Map(sepalWidth -> List(iris)),
              "petalLength" -> Map(petalLength -> List(iris)),
              "petalWidth" -> Map(petalWidth -> List(iris))
            ))
        }
        val dataList = data.toList

        LoadedData(dataList.map(_._1), dataList.map(_._2).reduceLeft(_ |+| _))
      }
    }(in => IO(in.close()))
  }

  def calculateWeights(params: Map[String, Map[Double, List[IrisAGDS]]], iris: IrisAGDS): Map[String, Map[Double, Double]] = {

    val weights: mutable.Map[String, mutable.Map[Double, Double]] =
      mutable.Map(
        "class" -> mutable.Map(),
        "sepalLength" -> mutable.Map(),
        "sepalWidth" -> mutable.Map(),
        "petalLength" -> mutable.Map(),
        "petalWidth" -> mutable.Map()
      )

    for {
      (param, paramValue) <- params
      (value, flowers) <- paramValue
    } yield {
      if (flowers.map(_.id).contains(iris.id)) {
        weights += (param -> mutable.Map())
        weights(param) += (value -> 1.0)
      }
    }

    for {
      (param, paramValue) <- params
    } yield {
      val mx = max(paramValue.keys)
      val mn = min(paramValue.keys)

      paramValue.keys.foreach { value =>
        iris.params.get(param).foreach { irisValue =>
          if (value != irisValue) {
            weights(param) += (value -> (1.0 - abs(value - irisValue) / (mx - mn)))
          }
        }
      }
    }

    weights.toMap.mapValues(_.toMap)
  }

  def getSimilarities(weights: Map[String, Map[Double, Double]], irises: List[IrisAGDS]) = {
    irises.map { iris =>
      val y = weights.flatMap { case (param, paramValues) =>
        iris.params.get(param).flatMap { irisValue => paramValues.get(irisValue).map(iris.weight * _) }
      }

      (iris.id, BigDecimal(y.sum * 100).setScale(2, RoundingMode.HALF_DOWN).doubleValue())
    }
  }
}

final case class IrisAGDS(id: Int,
                          className: String,
                          petalLength: Double,
                          sepalLength: Double,
                          petalWidth: Double,
                          sepalWidth: Double,
                          weight: Double = 0.2) {

  def params: Map[String, Double] = Map(
    "class" -> AGDS.irisMap(className),
    "petalLength" -> petalLength,
    "petalWidth" -> petalWidth,
    "sepalLength" -> sepalLength,
    "sepalWidth" -> sepalWidth
  )

  def show: String = {
    s"(id: $id, class: $className, $petalLength-$petalWidth-$sepalLength-$sepalWidth)"
  }
}