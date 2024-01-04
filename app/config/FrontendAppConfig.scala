/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.hmrcfrontend.config.ContactFrontendConfig
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, servicesConfig: ServicesConfig, contactFrontendConfig: ContactFrontendConfig) {

  val host: String    = configuration.get[String]("host")
  val appName: String = configuration.get[String]("appName")

  val loginUrl: String                 = configuration.get[String]("urls.login")
  val loginContinueUrl: String         = configuration.get[String]("urls.loginContinue")
  val signOutUrl: String               = configuration.get[String]("urls.signOut")
  val registerUrl: String              = configuration.get[String]("urls.register")
  val mdrGuidanceUrl: String           = configuration.get[String]("urls.mdrGuidance")
  val businessRulesGuidanceUrl: String = configuration.get[String]("urls.businessRulesGuidance")

  val enquireEmailLink: String = configuration.get[String]("urls.emailLink")

  lazy val homePageUrl: String = configuration.get[String]("urls.homepage")

  val upscanInitiateHost: String        = servicesConfig.baseUrl("upscan")
  val upscanRedirectBase: String        = configuration.get[String]("microservice.services.upscan.redirect-base")
  val upscanCallbackDelayInSeconds: Int = configuration.get[Int]("microservice.services.upscan.callbackDelayInSeconds")
  val upscanMaxFileSize: Int            = configuration.get[Int]("microservice.services.upscan.max-file-size-in-mb")

  val mdrUrl: String = servicesConfig.baseUrl("mandatory-disclosure-rules")

  val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en"),
    "cy" -> Lang("cy")
  )

  val timeout: Int   = configuration.get[Int]("timeout-dialog.timeout")
  val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  val spinnerCounter: Int = configuration.get[Int]("spinner.counter")
}
