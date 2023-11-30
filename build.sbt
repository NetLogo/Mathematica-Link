import java.nio.file.{ Files, Path, Paths }

name := "Mathematica-Link"

javaSource in Compile := baseDirectory.value / "src" / "main"

crossPaths := false

version := "6.1.2"

javacOptions ++=
  "-g -deprecation -encoding us-ascii -Werror -Xlint:all -Xlint:-serial -Xlint:-fallthrough -Xlint:-path --release 11 ".split(" ").toSeq

val netLogoVersion = settingKey[String]("version of NetLogo to depend on")

netLogoVersion := "6.2.0-d27b502"

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  "mathematica-link.jar"
}

// this section is copied from the NetLogo extension plugin
val netLogoDep = {
  val netLogoJarFile =
    Option(System.getProperty("netlogo.jar.file"))
      .map { f =>
        val jar = file(f)
        val testJar = file(f.replaceAllLiterally(".jar", "-tests.jar"))
        Seq(unmanagedJars in Compile ++= Seq(jar, testJar))
      }

  val netLogoJarURL =
    Option(System.getProperty("netlogo.jar.url"))
      .map { url =>
        val urlVersion = url.split("/").last
          .stripPrefix("NetLogo")
          .stripPrefix("-")
          .stripSuffix(".jar")
          val version = if (urlVersion == "") "DEV" else urlVersion
          val testUrl = url.replaceAllLiterally(".jar", "-tests.jar")
          Seq(libraryDependencies ++= Seq(
            "org.nlogo" % "NetLogo" % version changing() from url,
            "org.nlogo" % "NetLogo-tests" % version changing() from testUrl,
            "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5"
          ))
      }

  (netLogoJarFile orElse netLogoJarURL).getOrElse {
    Seq(
      resolvers += "netlogo" at "https://dl.cloudsmith.io/public/netlogo/netlogo/maven/",
      libraryDependencies ++= Seq(
        "org.nlogo" % "netlogo" % netLogoVersion.value intransitive,
        "org.nlogo" % "netlogo" % netLogoVersion.value % "test" intransitive() classifier "tests"))
  }
}

netLogoDep

libraryDependencies +=
  "com.wolfram.jlink" % "JLink" % "10.3.1" from s"file:///${(baseDirectory.value / "JLink.jar").toString}"

lazy val copyJLinkJar = taskKey[Unit]("copies the JLink.jar to local for easier builds")

copyJLinkJar := {
  // We could update this to switch to the Windows path when necessary, but this should work fine if we're
  // building out of the same folder for NetLogo releases.  -Jeremy B November 2020
  val jLinkJarDestPath = (baseDirectory.value / "JLink.jar").toPath
  if (!Files.exists(jLinkJarDestPath)) {
    val jLinkJarSources = List("/Applications/Mathematica.app/Contents/SystemFiles/Links/JLink/JLink.jar",
      "/Applications/Mathematica 2.app/Contents/SystemFiles/Links/JLink/JLink.jar")
    val jLinkJarSourcePaths = jLinkJarSources.map(Paths.get(_))
    val jLinkJarSourcePath: Option[Path] = jLinkJarSourcePaths.find(n => Files.exists(n))
    jLinkJarSourcePath match {
      case Some(s) => Files.copy(s, jLinkJarDestPath)
      case _ => throw new Exception(s"JLink.jar not found, is Mathematica installed? (${jLinkJarSourcePaths.mkString(",")})")
    }
  }
}

update := (update dependsOn copyJLinkJar).value
