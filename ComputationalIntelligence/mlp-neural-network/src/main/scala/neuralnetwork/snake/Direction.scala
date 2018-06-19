package neuralnetwork.snake

sealed trait Direction

case object UP extends Direction

case object DOWN extends Direction

case object LEFT extends Direction

case object RIGHT extends Direction