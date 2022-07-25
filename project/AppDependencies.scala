import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"            % "3.13.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % "5.21.0",
    "uk.gov.hmrc"       %% "play-language"                 % "5.2.0-play-28",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % "0.68.0",
    "com.typesafe.play" %% "play-json-joda"                % "2.7.4",
    "org.typelevel"     %% "cats-core"                     % "2.3.1",
    "org.julienrf"      %% "play-json-derived-codecs"      % "10.0.2"
  )

  val test = Seq(
    "org.scalatest"          %% "scalatest"               % "3.2.10",
    "org.scalatestplus"      %% "scalacheck-1-15"         % "3.2.10.0",
    "org.scalatestplus"      %% "mockito-3-4"             % "3.2.10.0",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0",
    "org.pegdown"             % "pegdown"                 % "1.6.0",
    "org.jsoup"               % "jsoup"                   % "1.14.3",
    "com.typesafe.play"      %% "play-test"               % PlayVersion.current,
    "org.mockito"            %% "mockito-scala"           % "1.16.46",
    "org.scalacheck"         %% "scalacheck"              % "1.15.4",
    "com.github.tomakehurst"  % "wiremock-jre8"           % "2.26.0",
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % "0.60.0",
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.62.2",
    "wolfendale"             %% "scalacheck-gen-regexp"   % "0.1.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
