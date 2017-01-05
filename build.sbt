name := "Mathematica-Link"

javaSource in Compile := baseDirectory.value / "src" / "main"

crossPaths := false

javacOptions ++=
  "-g -deprecation -encoding us-ascii -Werror -Xlint:all -Xlint:-serial -Xlint:-fallthrough -Xlint:-path -source 1.8 -target 1.8".split(" ").toSeq

val netLogoVersion = settingKey[String]("version of NetLogo to depend on")

netLogoVersion := "6.0.0"

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
            "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3"
          ))
      }

  (netLogoJarFile orElse netLogoJarURL).getOrElse {
    Seq(
      resolvers += Resolver.bintrayRepo("content/netlogo", "NetLogo-JVM"),
      libraryDependencies ++= Seq(
        "org.nlogo" % "netlogo" % netLogoVersion.value intransitive,
        "org.nlogo" % "netlogo" % netLogoVersion.value % "test" intransitive() classifier "tests"))
  }
}

netLogoDep

libraryDependencies +=
  "com.wolfram.jlink" % "JLink" % "10.3.0" from
    "file:///Applications/Mathematica.app/Contents/SystemFiles/Links/JLink/JLink.jar"
