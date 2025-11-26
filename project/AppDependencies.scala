import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.4.0"
  private val mongoVersion     = "2.10.0"
  private val playVersion      = "play-30"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% s"play-frontend-hmrc-$playVersion"            % "12.20.0",
    "uk.gov.hmrc"       %% s"play-conditional-form-mapping-$playVersion" % "3.3.0",
    "uk.gov.hmrc"       %% s"bootstrap-frontend-$playVersion"            % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-$playVersion"                    % mongoVersion,
    "org.typelevel"     %% "cats-core"                                   % "2.13.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-$playVersion"  % bootstrapVersion,
    "org.scalatestplus" %% "scalacheck-1-15"               % "3.2.11.0",
    "org.scalatestplus" %% "mockito-4-11"                   % "3.2.18.0",
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-test-$playVersion" % mongoVersion,
    "io.github.wolfendale"        %% "scalacheck-gen-regexp"         % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
