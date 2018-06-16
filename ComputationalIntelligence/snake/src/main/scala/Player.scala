import java.util.concurrent.atomic.AtomicReference

import scalafx.scene.Scene

trait Player {
  def chooseDirection(): Unit
}

object Player {
  def humanPlayer(scene: Scene, currentDirection: AtomicReference[Direction]) = new Player {
    override def chooseDirection(): Unit = {
???
    }
  }
}

// potrzebuje zmieniac aktualny kierunek i bedzie sie o to odpytywal snake animation