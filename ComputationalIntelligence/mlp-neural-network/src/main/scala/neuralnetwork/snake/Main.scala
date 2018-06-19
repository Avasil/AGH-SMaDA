package neuralnetwork.snake

import javafx.application.Application

object Main extends App {
    val snakeAnimation = new SnakeAnimation()

    Application.launch(snakeAnimation.getClass, args: _*)

//  val training = new SnakeTrainingFacility()
//  val trainingData = training.initialPopulation(10000)


}