import javafx.application.Application

object Main extends App {
  val snakeAnimation = new SnakeAnimation()

  Application.launch(snakeAnimation.getClass, args: _*)
}
