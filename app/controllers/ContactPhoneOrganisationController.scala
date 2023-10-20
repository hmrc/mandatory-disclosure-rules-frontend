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
import forms.ContactPhoneOrganisationFormProvider
import models.{Mode, Organisation, UserAnswers}
import navigation.ContactDetailsNavigator
import pages.{ContactNamePage, ContactPhonePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ContactPhoneOrganisationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContactPhoneOrganisationController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: ContactDetailsNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ContactPhoneOrganisationFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ContactPhoneOrganisationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form  = formProvider()
  val blank = ""

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData() andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.get(ContactPhonePage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, getContactName(request.userAnswers), mode))
  }

  private def getContactName(userAnswers: UserAnswers): String =
    userAnswers.get(ContactNamePage) match {
      case Some(contactName) => contactName
      case _                 => blank
    }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, getContactName(request.userAnswers), mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ContactPhonePage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(ContactPhonePage, Organisation, mode, updatedAnswers))
        )
  }
}
