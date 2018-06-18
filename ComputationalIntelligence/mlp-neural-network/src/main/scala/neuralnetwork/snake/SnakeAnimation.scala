package neuralnetwork.snake

import breeze.linalg.DenseVector
import cats.data.NonEmptyList
import javafx.animation.KeyFrame
import javafx.application.Application
import javafx.collections.ObservableList
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene._
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.{Font, Text}
import javafx.stage.Stage
import javafx.util.Duration
import neuralnetwork.lab1.LoadingData.InputData
import neuralnetwork.lab1.SnakeNN
import scalafx.animation.Timeline

import scala.collection.JavaConverters._

class SnakeAnimation extends Application with SnakeGame with NNUtilities {

  val timeline = new Timeline()
  var snake: ObservableList[Node] = _
  var isRunning = false
  var score = 0

  def snakeAsVector(snake: ObservableList[Node]): NonEmptyList[DenseVector[Double]] = {
    val head :: tail = snake.asScala.toList

    NonEmptyList(head, tail).map(node => DenseVector(node.getTranslateX, node.getTranslateY))
  }

  def nodeAsVector(node: Node): DenseVector[Double] =
    DenseVector(node.getTranslateX, node.getTranslateY)

  def generateFood(food: Rectangle, snake: ObservableList[Node]): Unit = {
    val vectorSnake = snakeAsVector(snake)
    val newFood = generateFood(vectorSnake)

    food.setTranslateX(newFood(0))
    food.setTranslateY(newFood(1))
  }

  def describeCurrentState(food: Node): CurrentState = {
    val vectorSnake = snakeAsVector(snake)
    val foodAsVector = nodeAsVector(food)
    describeCurrentState(isRunning, score, foodAsVector, vectorSnake)
  }

  def createContent(player: Player): Parent = {
    val root = new Pane()
    root.setPrefSize(BOARD_WIDTH, BOARD_HEIGHT)

    val snakeBody = new Group()
    snake = snakeBody.getChildren
    if (snake.size() < 1) {
      val genX = (Math.random() * (BOARD_WIDTH - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
      val genY = (Math.random() * (BOARD_HEIGHT - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
      val head = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
      val tail = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
      head.setTranslateX(genX)
      head.setTranslateY(genY)
      tail.setTranslateX(genX + BLOCK_SIZE)
      tail.setTranslateY(genY)
      snake.add(head)
      snake.add(tail)
    }

    val food = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
    food.setFill(Color.BLUE)
    generateFood(food, snake)

    val text = new Text()
    text.setFill(Color.RED)
    text.setFont(Font.font("Courier New", BLOCK_SIZE * 0.75))
    text.setX(BOARD_WIDTH - 5 * BLOCK_SIZE)
    text.setY(BOARD_HEIGHT - BLOCK_SIZE)

    val frame = new KeyFrame(Duration.seconds(0.05), new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        if (isRunning) {
          text.setText(s"Score: $score")
          val newDirection = player.chooseDirection(describeCurrentState(food))
          val tail = snake.remove(snake.size() - 1)

          newDirection match {
            case UP =>
              tail.setTranslateX(snake.get(0).getTranslateX)
              tail.setTranslateY(snake.get(0).getTranslateY - BLOCK_SIZE)
            case DOWN =>
              tail.setTranslateX(snake.get(0).getTranslateX)
              tail.setTranslateY(snake.get(0).getTranslateY + BLOCK_SIZE)
            case LEFT =>
              tail.setTranslateX(snake.get(0).getTranslateX - BLOCK_SIZE)
              tail.setTranslateY(snake.get(0).getTranslateY)
            case RIGHT =>
              tail.setTranslateX(snake.get(0).getTranslateX + BLOCK_SIZE)
              tail.setTranslateY(snake.get(0).getTranslateY)
          }

          snake.add(0, tail)

          if (detectCollision()) stopGame(player, food)
          else if (detectFood(tail, food)) {
            expandSnake(tail.getTranslateX, tail.getTranslateY)
            generateFood(food, snake)
          }
        }
      }
    })

    timeline.getKeyFrames.add(frame)
    timeline.setCycleCount(Timeline.Indefinite)

    root.getChildren.addAll(food, snakeBody, text)

    root
  }

  def detectCollision(): Boolean =
    detectCollision(snakeAsVector(snake))

  def detectFood(tail: Node, food: Rectangle): Boolean =
    tail.getTranslateX == food.getTranslateX && tail.getTranslateY == food.getTranslateY

  def expandSnake(newTailX: Double, newTailY: Double): Unit = {
    score += 1

    val rect = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
    rect.setTranslateX(newTailX)
    rect.setTranslateY(newTailY)

    snake.add(rect)
  }

  def stopGame(player: Player, food: Rectangle) {
    isRunning = false
    val lastState = describeCurrentState(food)
    timeline.stop()
    generateFood(food, snake)
    snake.clear()
    if (player.gameFinished(lastState))
      startGame(player)
  }

  def startGame(player: Player) {
    score = 0
    if (snake.size() < 1) {
      val genX = (Math.random() * (BOARD_WIDTH - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
      val genY = (Math.random() * (BOARD_HEIGHT - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
      val head = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
      val tail = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
      head.setTranslateX(genX)
      head.setTranslateY(genY)
      tail.setTranslateX(genX + BLOCK_SIZE)
      tail.setTranslateY(genY)
      snake.add(head)
      snake.add(tail)
    }
    timeline.play()
    isRunning = true
  }

  override def start(primaryStage: Stage) {
    val training = new SnakeTrainingFacility()


    val trainingData: List[InputData] = training.initialPopulation(10000).map(d =>
      InputData(DenseVector(d.previousObservation.features.data :+ d.action.toDouble), DenseVector(d.reward.toDouble))
    )

    val trainedNetwork = SnakeNN.snakeNet(trainingData)

    val player = Player.neuralNetworkPlayer(RIGHT, trainedNetwork)

    //    val player = Player.humanPlayer(RIGHT)
    val scene = new Scene(createContent(player))

    scene.setOnKeyPressed(new EventHandler[input.KeyEvent] {
      override def handle(event: input.KeyEvent): Unit = player.onKeyPressed(event)
    })

    primaryStage.setTitle("Snake Game - Piotr Gawryś")
    primaryStage.setScene(scene)
    primaryStage.show()
    startGame(player)
  }
}