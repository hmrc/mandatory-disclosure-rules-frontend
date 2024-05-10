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

package controllers

import controllers.actions._
import forms.HaveSecondContactFormProvider
import models.{AffinityType, CheckMode, UserAnswers}
import navigation.ContactDetailsNavigator
import pages.{ContactNamePage, HaveSecondContactPage, SecondContactNamePage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CommonUtils
import views.html.HaveSecondContactView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HaveSecondContactController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: ContactDetailsNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: HaveSecondContactFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: HaveSecondContactView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData() andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.get(HaveSecondContactPage) match {
        case Some(value) => form.fill(value)
        case None =>
          request.userAnswers.get(SecondContactNamePage) match {
            case Some(_) => form.fill(true)
            case _       => form.fill(false)
          }
      }

      Ok(view(preparedForm, getContactName(request.userAnswers)))
  }

  private def getContactName(userAnswers: UserAnswers)(implicit messages: Messages): String =
    userAnswers.get(ContactNamePage) match {
      case Some(contactName) => contactName
      case _                 => messages(CommonUtils.firstContactName)
    }

  def onSubmit(): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, getContactName(request.userAnswers)))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(HaveSecondContactPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(HaveSecondContactPage, AffinityType(request.userType), CheckMode, updatedAnswers))
        )
  }
}
