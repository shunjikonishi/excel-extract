name := "excel-extract"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.apache.poi" % "poi" % "3.10-FINAL",
  "org.apache.poi" % "poi-ooxml" % "3.10-FINAL",
  "org.apache.poi" % "ooxml-schemas" % "1.1",
  "com.google.code.gson" % "gson" % "2.2.4"
)     

play.Project.playScalaSettings
