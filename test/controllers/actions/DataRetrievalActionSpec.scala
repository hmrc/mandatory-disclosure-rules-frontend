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

package controllers.actions

import base.{SpecBase, TestValues}
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.test.FakeRequest
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase {

  class Harness(sessionRepository: SessionRepository) extends DataRetrievalActionProvider(sessionRepository) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" - {

    "when there is no data in the cache" - {

      "must set userAnswers to 'None' in the request" in {

        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get(TestValues.id)) thenReturn Future(None)
        val action = new Harness(sessionRepository)

        val result = action.callTransform(IdentifierRequest(FakeRequest(), TestValues.id, TestValues.subscriptionId, Organisation)).futureValue

        result.userAnswers must not be defined
      }
    }

    "when there is data in the cache" - {

      "must build a userAnswers object and add it to the request" in {

        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get(TestValues.id)) thenReturn Future(Some(UserAnswers(TestValues.id)))
        val action = new Harness(sessionRepository)

        val result = action.callTransform(new IdentifierRequest(FakeRequest(), TestValues.id, TestValues.subscriptionId, Organisation)).futureValue

        result.userAnswers mustBe defined
      }
    }
  }
}
