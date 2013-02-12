import AssemblyKeys._ // put this at the top of the file

name := "ExSiter"

version := "0.9"

scalaVersion := "2.10.0"

assemblySettings

libraryDependencies += "com.typesafe" % "config" % "1.0.0"

libraryDependencies += "com.jcraft" % "jsch" % "0.1.49"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"
