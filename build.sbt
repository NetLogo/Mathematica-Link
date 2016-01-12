name := "Mathematica-Link"

javaSource in Compile := baseDirectory.value / "src" / "main"

crossPaths := false

javacOptions ++=
  "-g -deprecation -encoding us-ascii -Werror -Xlint:all -Xlint:-serial -Xlint:-fallthrough -Xlint:-path -source 1.6 -target 1.6".split(" ").toSeq

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  "mathematica-link.jar"
}

libraryDependencies ++= Seq(
  "com.wolfram.jlink" % "JLink" % "10.3.0" from
    "file:///Applications/Mathematica.app/Contents/SystemFiles/Links/JLink/JLink.jar",
  "org.nlogo" % "NetLogo" % "5.3.0" from
    "https://s3.amazonaws.com/ccl-artifacts/NetLogo-5.3.0.jar"
)
