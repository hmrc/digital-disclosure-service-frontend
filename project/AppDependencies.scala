import sbt.*

object AppDependencies {
  val bootstrapVersion = "7.23.0"
  val mongoVersion = "1.3.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "uk.gov.hmrc"       %% "domain"                         % "8.3.0-play-28",
    "uk.gov.hmrc"       %% "emailaddress"                   % "3.8.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "7.29.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"             % mongoVersion,
    "org.typelevel"     %% "cats-core"                      % "2.8.0",
    "uk.gov.hmrc"       %% "tax-year"                       % "3.3.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrapVersion,
    "org.scalatestplus" %% "scalacheck-1-15" % "3.2.10.0",
    "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0",
    "org.scalamock" %% "scalamock" % "5.1.0",
    "org.jsoup" % "jsoup" % "1.14.3",
    "org.mockito" %% "mockito-scala" % "1.16.42",
    "com.github.chocpanda" %% "scalacheck-magnolia" % "0.5.1",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-28" % mongoVersion,
    "com.vladsch.flexmark" % "flexmark-all" % "0.62.2",
    "com.github.tomakehurst" % "wiremock-standalone" % "2.27.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
