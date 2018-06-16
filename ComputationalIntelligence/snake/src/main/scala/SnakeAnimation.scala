import config._
import javafx.animation.KeyFrame
import javafx.application.Application
import javafx.collections.ObservableList
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.{Font, Text}
import javafx.scene.{Group, Node, Parent, Scene}
import javafx.stage.Stage
import javafx.util.Duration
import scalafx.animation.Timeline

class SnakeAnimation extends Application {

  val timeline = new Timeline()

  var snake: ObservableList[Node] = _
  var isMoving = false
  var isRunning = false
  var currentDirection: Direction = RIGHT
  var score = 0

  def generateFood(food: Rectangle, snake: ObservableList[Node]): Unit = {
    var correctPos = false

    val snakePositions: List[(Double, Double)] = {
      var list = List[(Double, Double)]()
      snake.forEach { rect =>
        list :+= (rect.getTranslateX, rect.getTranslateY)
      }
      list
    }

    while (!correctPos) {
      val newX = (Math.random() * (BOARD_WIDTH - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
      val newY = (Math.random() * (BOARD_HEIGHT - BLOCK_SIZE)).toInt / BLOCK_SIZE * BLOCK_SIZE
      if (!snakePositions.contains((newX, newY))) {
        correctPos = true
        food.setTranslateX(newX)
        food.setTranslateY(newY)
      }
    }
  }

  def createContent(): Parent = {
    val root = new Pane()
    root.setPrefSize(BOARD_WIDTH, BOARD_HEIGHT)

    val snakeBody = new Group()
    snake = snakeBody.getChildren

    val food = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
    food.setFill(Color.BLUE)
    generateFood(food, snake)

    val text = new Text()
    text.setFill(Color.RED)
    text.setFont(Font.font("Courier New", BLOCK_SIZE * 0.75))
    text.setX(BOARD_WIDTH - 5 * BLOCK_SIZE)
    text.setY(BOARD_HEIGHT - BLOCK_SIZE)

    val frame = new KeyFrame(Duration.seconds(0.2), _ => {
      if (isRunning) {
        text.setText(s"Score: $score")

        val toRemove = snake.size() > 1
        val tail = if (toRemove) snake.remove(snake.size() - 1) else snake.get(0) // last element

        // at every iteration I go with currentDirection
        // maybe I could ask for direction instead? ;)
        currentDirection match {
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

        //        isMoving = true

        if (toRemove)
          snake.add(0, tail) // transfering tail to the front ???

        if (detectCollision(tail)) restartGame()
        else if (detectFood(tail, food)) {
          expandSnake(tail.getTranslateX, tail.getTranslateY)
          generateFood(food, snake)
        }

        //        if (isMoving) {
        //          val r = (Math.random() * 100).toInt % 4
        //          println(r)
        //
        //          r match {
        //            case 0 if currentDirection != DOWN => currentDirection = UP
        //            case 1 if currentDirection != UP => currentDirection = DOWN
        //            case 2 if currentDirection != RIGHT => currentDirection = LEFT
        //            case 3 if currentDirection != LEFT => currentDirection = RIGHT
        //            case _ =>
        //
        //          }
        //          isMoving = false
        //        }
      } // return ?
    })

    timeline.getKeyFrames.add(frame)
    timeline.setCycleCount(Timeline.Indefinite)

    root.getChildren.addAll(food, snakeBody, text)

    root
  }

  def detectCollision(tail: Node): Boolean = {
    import scala.collection.JavaConverters._
    val tailX = tail.getTranslateX
    val tailY = tail.getTranslateY

    tailX < 0 ||
      tailX >= BOARD_WIDTH ||
      tailY < 0 ||
      tailY >= BOARD_HEIGHT ||
      snake.asScala.foldLeft(false) { case (detected, node) =>
        detected || (node != tail && tailX == node.getTranslateX && tailY == node.getTranslateY)
      }
  }

  def detectFood(tail: Node, food: Rectangle): Boolean =
    tail.getTranslateX == food.getTranslateX && tail.getTranslateY == food.getTranslateY

  def expandSnake(tailX: Double, tailY: Double): Boolean = {
    score += 1
    val rect = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
    rect.setTranslateX(tailX)
    rect.setTranslateY(tailY)

    snake.add(rect)
  }

  def restartGame() {
    stopGame()
    startGame()
  }

  def stopGame() {
    isRunning = false
    timeline.stop()
    snake.clear()
  }

  def startGame() {
    score = 0
    currentDirection = RIGHT
    val head = new Rectangle(BLOCK_SIZE, BLOCK_SIZE)
    snake.add(head)
    timeline.play()
    isRunning = true
  }

  override def start(primaryStage: Stage) {
    import javafx.scene.input.KeyCode.{A, D, S, W}

    val scene = new Scene(createContent())
    // todo: zwracanie pozycji jedzenia, score'u i snake'a
    // to mozna zamockowac na własną grę, randomową, przez sieć
    // make gui optional

    scene.setOnKeyPressed(event => {
      //      if (isMoving) {
      event.getCode match {
        case W if currentDirection != DOWN => currentDirection = UP
        case S if currentDirection != UP => currentDirection = DOWN
        case A if currentDirection != RIGHT => currentDirection = LEFT
        case D if currentDirection != LEFT => currentDirection = RIGHT
        case _ =>

        //        }
        //        isMoving = false
      }
    })

    primaryStage.setTitle("Snake Game - Piotr Gawryś")
    primaryStage.setScene(scene)
    primaryStage.show()
    startGame()
  }
}