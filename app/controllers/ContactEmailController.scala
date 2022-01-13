/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.ContactEmailFormProvider
import models.{AffinityType, Mode, Organisation, UserAnswers}
import navigation.ContactDetailsNavigator
import pages.{ContactEmailPage, ContactNamePage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ContactEmailView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContactEmailController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: ContactDetailsNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ContactEmailFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ContactEmailView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, affinityType: AffinityType): Action[AnyContent] = (identify andThen getData.apply() andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.get(ContactEmailPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, affinityType, getContactName(request.userAnswers, affinityType), mode))
  }

  private def getContactName(userAnswers: UserAnswers, affinityType: AffinityType)(implicit messages: Messages): String =
    (userAnswers.get(ContactNamePage), affinityType) match {
      case (Some(contactName), Organisation) => contactName
      case _                                 => messages("default.firstContact.name")
    }

  def onSubmit(mode: Mode, affinityType: AffinityType): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, affinityType, getContactName(request.userAnswers, affinityType), mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ContactEmailPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(ContactEmailPage, affinityType, mode, updatedAnswers))
        )
  }

}
