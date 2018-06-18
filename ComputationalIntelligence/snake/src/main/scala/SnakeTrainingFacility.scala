import breeze.linalg._
import cats.data.NonEmptyList
import config._
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.conf.{MultiLayerConfiguration, NeuralNetConfiguration}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Nesterovs
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.platanios.tensorflow.api.{Tensor, _}
import org.platanios.tensorflow.api.ops.io.data.TensorSlicesDataset
import org.platanios.tensorflow.api.tf.learn._

import scala.collection.mutable

class SnakeTrainingFacility extends SnakeGame with NNUtilities {
  var isRunning = false
  var score = 0
  val learningRate = 0.01


  def tensorFlowModel(trainingData: List[TrainingResult]) = {
    val features = trainingData.toArray.map(d => d.previousObservation.features.data :+ d.action.toDouble)
    val labels = trainingData.toArray.map(_.reward.toDouble)

    val featuresTensor = Tensor(features.head, features.tail: _*)
    val labelsTensor = Tensor(labels.head, labels.tail: _*)

    val trainFeatures = tf.data.TensorSlicesDataset(featuresTensor)
    val trainLabels = tf.data.TensorSlicesDataset(labelsTensor)

    val trainData =
      trainFeatures.zip(trainLabels)
        .repeat()
        .shuffle(10000)
        .batch(256)
        .prefetch(10)

    // Create the MLP model.
    val input = Input(FLOAT64, Shape(-1, 5, 1))
    val trainInput = Input(FLOAT64, Shape(-1))

    val layer = tf.learn.Flatten("Input/Flatten") >>
      tf.learn.Cast("Input/Cast", FLOAT64) >>
      tf.learn.Linear("Layer_0/Linear", 25) >> tf.learn.ReLU("Layer_0/ReLU", 0.1f) >>
      tf.learn.Linear("OutputLayer/Linear", 1)

    val trainingInputLayer = Cast("TrainInput/Cast", FLOAT64)
    val loss = SparseSoftmaxCrossEntropy("Loss/CrossEntropy") >>
      tf.learn.Mean("Loss/Mean") >> tf.learn.ScalarSummary("Loss/Summary", "Loss")
    val optimizer = tf.train.Adam(learningRate)
    val model = Model.supervised(input, layer, trainInput, trainingInputLayer, loss, optimizer)

    // Create an estimator and train the model.
    val estimator = InMemoryEstimator(model)
    estimator.train(() => trainData, StopCriteria(maxSteps = Some(1000000)))

    estimator
  }

  def model(trainingData: List[TrainingResult]): MultiLayerNetwork = {
    val rngSeed = 1337

    val conf: MultiLayerConfiguration = new NeuralNetConfiguration.Builder()
      .seed(rngSeed)
      .weightInit(WeightInit.XAVIER)
      .activation(Activation.RELU)
      .optimizationAlgo(OptimizationAlgorithm.LINE_GRADIENT_DESCENT)
      .updater(new Nesterovs(learningRate, 0.9))
      .regularization(true).l2(1e-4)
      .list()
      .layer(0, new DenseLayer.Builder().nIn(5).nOut(25).activation(Activation.RELU).weightInit(WeightInit.XAVIER).build())
      //      .layer(1, new DenseLayer.Builder().nIn(45).nOut(25).activation(Activation.RELU).weightInit(WeightInit.XAVIER).build())
      .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE).nIn(25).nOut(1).activation(Activation.SIGMOID).weightInit(WeightInit.XAVIER).build())
      .pretrain(false)
      .backprop(true)
      .build()

    val model = new MultiLayerNetwork(conf)
    model.init()
    //    model.setListeners(new ScoreIterationListener(1))

    val trainingSet = trainingData.toArray[TrainingResult].map(d => d.previousObservation.features.data :+ d.action.toDouble)
    val features: INDArray = Nd4j.create(trainingSet)
    val rewards: INDArray = Nd4j.create(Array(trainingData.map(_.reward.toDouble).toArray)).reshape(-1, 1)

    val batchSize = trainingData.size / 10
    val iterator: DataSetIterator = new ListDataSetIterator(new DataSet(features, rewards).asList(), batchSize)
    for (_ <- 0 to 10) {
      iterator.reset()
      model.fit(iterator)
      //      model.fit(features, rewards)
    }

    model
  }

  def start(): CurrentState = {
    val genX = (Math.random() * (BOARD_WIDTH - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
    val genY = (Math.random() * (BOARD_HEIGHT - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE

    val snake: NonEmptyList[DenseVector[Double]] = NonEmptyList(DenseVector(genX, genY), List(DenseVector(genX + BLOCK_SIZE, genY)))
    val food = generateFood(snake)
    isRunning = true
    score = 0

    describeCurrentState(isRunning, score, food, snake)
  }

  def step(snake: NonEmptyList[DenseVector[Double]], direction: Direction, food: DenseVector[Double]): CurrentState = {
    // need to remove tail
    val oldTail: DenseVector[Double] = snake.tail.last
    val newSnake = moveSnake(snake, direction)
    val collisionCheck = detectCollision(newSnake)

    if (collisionCheck) {
      isRunning = false
      describeCurrentState(isRunning, score, food, newSnake)
    } else if (detectFood(newSnake.head, food)) {
      val expandedSnake = expandSnake(newSnake.tail, oldTail(0), oldTail(1))
      score += 1
      val updatedSnake = NonEmptyList(newSnake.head, expandedSnake)
      val newFood = generateFood(updatedSnake)

      describeCurrentState(isRunning, score, newFood, updatedSnake)
    } else {
      describeCurrentState(isRunning, score, food, newSnake)
    }
  }

  def moveSnake(snake: NonEmptyList[DenseVector[Double]], direction: Direction): NonEmptyList[DenseVector[Double]] = {
    val oldHeadX = snake.head(0)
    val oldHeadY = snake.head(1)
    val newHead = direction match {
      case UP =>
        DenseVector(oldHeadX, oldHeadY - BLOCK_SIZE)
      case DOWN =>
        DenseVector(oldHeadX, oldHeadY + BLOCK_SIZE)
      case LEFT =>
        DenseVector(oldHeadX - BLOCK_SIZE, oldHeadY)
      case RIGHT =>
        DenseVector(oldHeadX + BLOCK_SIZE, oldHeadY)
    }
    NonEmptyList(newHead, List(snake.head) ++ snake.tail.dropRight(1))
  }

  def initialPopulation(initialGames: Int): List[TrainingResult] = {
    val trainingData = mutable.Buffer[TrainingResult]()
    (0 to initialGames).foreach { _ =>
      val CurrentState(_, prevScore, snake, food) = start()
      var currentSnake = snake
      var previousObservation: CurrentObservation = genCurrentObservation(currentSnake, food)
      var previousFoodDistance: Double = getFoodDistance(food, currentSnake.head)
      var maxSteps = 1000
      while (isRunning && maxSteps > 0) {
        val (action, newDirection) =
        //          if (previousObservation.angle == 0.0) (0, getGameAction(currentSnake, 0))
        //          else if(previousObservation.angle < 0.0) (-1, getGameAction(currentSnake, -1))
        //          else (1, getGameAction(currentSnake, 1))
          generateAction(currentSnake)
        val CurrentState(running, newScore, newSnake, newFood) = step(currentSnake, newDirection, food)
        currentSnake = newSnake
        if (!running) {
          trainingData.append(TrainingResult(previousObservation, action, -25))
        } else {
          val newFoodDistance = getFoodDistance(newFood, currentSnake.head)
          if (newScore > prevScore) {
            trainingData.append(TrainingResult(previousObservation, action, 3))
          } else if (newFoodDistance < previousFoodDistance) {
            trainingData.append(TrainingResult(previousObservation, action, 1))
          } else {
            trainingData.append(TrainingResult(previousObservation, action, 0))
          }
          previousObservation = genCurrentObservation(currentSnake, newFood)
          previousFoodDistance = newFoodDistance
        }

        maxSteps -= 1
      }
    }
    trainingData.toList
  }

}

final case class TrainingResult(previousObservation: CurrentObservation, action: Int, reward: Int)