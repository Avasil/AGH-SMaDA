name := "mlp-neural-network"

version := "0.1"


scalaVersion := "2.11.11"

val breezeVersion = "0.13.2"

val breeze = Seq(
  "org.scalanlp" %% "breeze" % breezeVersion,
  "org.scalanlp" %% "breeze-natives" % breezeVersion,
  "org.scalanlp" %% "breeze-viz" % breezeVersion
)

val fp = Seq(
  "org.typelevel" %% "cats-effect" % "1.0.0-RC2"
)

libraryDependencies  ++= breeze ++ fp

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"