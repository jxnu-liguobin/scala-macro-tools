import org.jetbrains.sbtidea.Keys._

name := "scala-macro-tools-intellij-plugin"
organization := "org.bitlap"

lazy val scala213 = "2.13.6"

scalaVersion := scala213

lazy val `intellij-plugin` = (project in file("."))
  .enablePlugins(SbtIdeaPlugin)
  .settings(
    version := (ThisBuild / version).value,
    scalaVersion := scala213,
    ThisBuild / intellijPluginName := "Scala-Macro-Tools",
    ThisBuild / intellijBuild := "211.7628.21", // @see https://confluence.jetbrains.com/display/IDEADEV/IDEA+2021.1+latest+builds
    ThisBuild / intellijPlatform := IntelliJPlatform.IdeaCommunity,
    Global / intellijAttachSources := true,
    Compile / javacOptions ++= "--release" :: "11" :: Nil,
    //    Global / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings"),
    intellijPlugins ++= Seq("com.intellij.java", "com.intellij.java-i18n", "org.intellij.scala").map(_.toPlugin),
    libraryDependencies ++= Seq.empty,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources",
    Test / unmanagedResourceDirectories += baseDirectory.value / "src" / "test" / "resources",
    patchPluginXml := pluginXmlOptions { xml =>
      xml.version = (ThisBuild / version).value
      xml.pluginDescription = IO.read(baseDirectory.value / "src" / "main" / "resources" / "patch" / "description.html")
      xml.changeNotes = IO.read(baseDirectory.value / "src" / "main" / "resources" / "patch" / "change.html")
    },
    publish / skip := true,
  )
