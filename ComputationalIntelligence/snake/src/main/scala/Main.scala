import javafx.application.Application
import org.platanios.tensorflow.api.{Tensor, _}

object Main extends App {
    val snakeAnimation = new SnakeAnimation()

    Application.launch(snakeAnimation.getClass, args: _*)

//  val training = new SnakeTrainingFacility()
//  val trainingData = training.initialPopulation(10000)
//
//  val test = CurrentObservation(true, false, true, 0.0).features
//  val options = Array(test.data :+ -1.0, test.data :+ 0.0, test.data :+ 1.0)

//  val trainedNetwork: MultiLayerNetwork = training.model(trainingData)
//  val predict = trainedNetwork.output(Nd4j.create(options), false)


//  val trainedNetwork = training.tensorFlowModel(trainingData)
//  val featuresTensor = Tensor(options.head, options.tail: _*)
//  val prediction = trainedNetwork.infer(() => featuresTensor)
//  val predict = prediction.cast(FLOAT64).scalar.asInstanceOf[Double]

//  println(predict)
}