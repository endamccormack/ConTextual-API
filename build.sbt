name := "ConTextualAPI"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

resolvers ++= Seq(
  "RoundEights" at "http://maven.spikemark.net/roundeights"
)

libraryDependencies ++= Seq(
  "com.roundeights" %% "hasher" % "1.0.0",
  "mysql" % "mysql-connector-java" % "5.1.18"
)


play.Project.playScalaSettings
