import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "public")

name := "play-vue-webpack-spa"

version := "1.1"

scalaVersion := "2.12.2"

lazy val `play-vue-webpack-spa` = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(guice, filters, jdbc , cacheApi ,ws , specs2 % Test)
libraryDependencies += javaJdbc
libraryDependencies ++= Seq( javaJdbc, "mysql" % "mysql-connector-java" % "8.0.9-rc")
libraryDependencies += "commons-dbcp" % "commons-dbcp" % "1.4"
libraryDependencies += guice
libraryDependencies += "org.jdbi" % "jdbi3-core" % "3.1.1"
libraryDependencies += "org.jdbi" % "jdbi3-sqlobject" % "3.1.1"

// Play framework hooks for development
PlayKeys.playRunHooks += WebpackServer(file("./front"))

unmanagedResourceDirectories in Test +=  baseDirectory ( _ /"target/web/public/test" ).value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

// Production front-end build
lazy val cleanFrontEndBuild = taskKey[Unit]("Remove the old front-end build")

cleanFrontEndBuild := {
  val d = file("public/bundle")
  if (d.exists()) {
    d.listFiles.foreach(f => {
      if(f.isFile) f.delete
    })
  }
}

lazy val frontEndBuild = taskKey[Unit]("Execute the npm build command to build the front-end")

frontEndBuild := {
  println(Process("npm install", file("front")).!!)
  println(Process("npm run build", file("front")).!!)
}

frontEndBuild := (frontEndBuild dependsOn cleanFrontEndBuild).value

dist := (dist dependsOn frontEndBuild).value
