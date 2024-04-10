import sbt.*

object AppDependencies {
  val bootstrapVersion = "8.5.0"
  val mongoVersion = "1.8.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"     % bootstrapVersion,
    "uk.gov.hmrc"       %% "domain-play-30"                 % "9.0.0",
    "uk.gov.hmrc"       %% "emailaddress"                   % "3.8.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"     % "8.5.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"             % mongoVersion,
    "org.typelevel"     %% "cats-core"                      % "2.8.0",
    "uk.gov.hmrc"       %% "tax-year"                       % "4.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-30"  % bootstrapVersion,
    "org.scalatestplus"    %% "scalacheck-1-17"         % "3.2.17.0",
    "org.scalamock"        %% "scalamock"               % "5.1.0",
    "org.jsoup"            %   "jsoup"                  % "1.14.3",
    "com.github.chocpanda" %% "scalacheck-magnolia"     % "0.5.1",
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-30" % mongoVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
