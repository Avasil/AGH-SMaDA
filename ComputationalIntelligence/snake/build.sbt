name := "snake"

version := "0.1"

scalaVersion := "2.12.6"

resolvers += Resolver.sonatypeRepo("snapshots")

val breezeVersion = "0.13.2"

val breeze = Seq(
  "org.scalanlp" %% "breeze" % breezeVersion,
  "org.scalanlp" %% "breeze-natives" % breezeVersion,
  "org.scalanlp" %% "breeze-viz" % breezeVersion
)

libraryDependencies += "org.tensorflow" % "tensorflow" % "1.8.0"
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0"
libraryDependencies ++= breeze

unmanagedBase := baseDirectory.value / "libs"

javaOptions in run += s"-Djava.library.path=libs/tensorflow_jni.dll"
fork in run := true