import javafx.application.Application
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.factory.Nd4j

object Main extends App {
  val snakeAnimation = new SnakeAnimation()

  Application.launch(snakeAnimation.getClass, args: _*)

//  val training = new SnakeTrainingFacility()
//  val trainingData = training.initialPopulation(10000)
//  val trainedNetwork: MultiLayerNetwork = training.model(trainingData)
//
//  val test = CurrentObservation(true, false, true, 0.0).features
//  val options = Array(test.data :+ -1.0, test.data :+ 0.0, test.data :+ 1.0)
//
//  println(trainedNetwork.output(Nd4j.create(options), false))
}