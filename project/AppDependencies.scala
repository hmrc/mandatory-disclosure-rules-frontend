import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.5.0"
  private val mongoVersion     = "1.9.0"
  private val playVersion      = "play-30"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% s"play-frontend-hmrc-$playVersion"            % "8.5.0",
    "uk.gov.hmrc"       %% s"play-conditional-form-mapping-$playVersion" % "2.0.0",
    "uk.gov.hmrc"       %% s"bootstrap-frontend-$playVersion"            % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-$playVersion"                    % mongoVersion,
    "org.typelevel"     %% "cats-core"                                   % "2.10.0",
    "org.julienrf"      %% "play-json-derived-codecs"                    % "11.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"         %% s"bootstrap-test-$playVersion"  % bootstrapVersion,
    "org.scalatestplus"   %% "scalacheck-1-15"               % "3.2.11.0",
    "org.scalatestplus"   %% "mockito-3-4"                   % "3.2.10.0",
    "org.pegdown"          % "pegdown"                       % "1.6.0",
    "org.jsoup"            % "jsoup"                         % "1.17.2",
    "org.mockito"         %% "mockito-scala"                 % "1.17.31",
    "org.scalacheck"      %% "scalacheck"                    % "1.18.0",
    "uk.gov.hmrc.mongo"   %% s"hmrc-mongo-test-$playVersion" % mongoVersion,
    "com.vladsch.flexmark" % "flexmark-all"                  % "0.64.8",
    "wolfendale"          %% "scalacheck-gen-regexp"         % "0.1.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
