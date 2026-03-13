import sbt.*

object AppDependencies {
  val bootstrapVersion = "10.5.0"
  val mongoVersion = "2.7.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"     % bootstrapVersion,
    "uk.gov.hmrc"       %% "domain-play-30"                 % "12.1.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"     % "12.22.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"             % mongoVersion,
    "org.typelevel"     %% "cats-core"                      % "2.12.0",
    "uk.gov.hmrc"       %% "tax-year"                       % "6.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-30"  % bootstrapVersion,
    "org.scalatestplus"    %% "scalacheck-1-17"         % "3.2.18.0",
    "org.scalamock"        %% "scalamock"               % "7.5.3",
    "org.jsoup"            %   "jsoup"                  % "1.22.1",
    "io.github.martinhh"   %% "scalacheck-derived"      % "0.6.0",
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-30" % mongoVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}