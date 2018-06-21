name := "snake"

version := "0.1"

scalaVersion := "2.11.11"

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

val breezeVersion = "0.13.2"

val breeze = Seq(
  "org.scalanlp" %% "breeze" % breezeVersion,
  "org.scalanlp" %% "breeze-natives" % breezeVersion,
  "org.scalanlp" %% "breeze-viz" % breezeVersion
)

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0"
libraryDependencies ++= breeze

val nd4jVersion = "0.9.1"

libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % nd4jVersion
libraryDependencies += "org.nd4j" % "nd4j-native-platform" % nd4jVersion
libraryDependencies += "org.nd4j" %% "nd4s" % nd4jVersion

unmanagedBase := baseDirectory.value / "libs"

fork in run := true