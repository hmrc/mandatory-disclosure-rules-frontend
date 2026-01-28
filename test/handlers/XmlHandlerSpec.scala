/*
 * Copyright 2026 HM Revenue & Customs
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

package handlers

import base.SpecBase
import org.scalatest.freespec.AnyFreeSpec
import scala.xml.Elem
import java.io.File

class XmlHandlerSpec extends SpecBase {

  val handler = new XmlHandler

  "XmlHandler" - {

    "load must correctly parse a local XML file" in {
      val file = new File("test/resources/test.xml")
      val url  = file.toURI.toURL.toString

      val xml: Elem = handler.load(url)

      (xml \ "child").text mustEqual "value"
      xml.label mustEqual "root"
    }

    "load must throw an exception for invalid file" in {
      val invalidDirectory = "non-existent-directory/test.xml"

      intercept[Exception] {
        handler.load(invalidDirectory)
      }
    }

    "load must throw an exception for non-XML content" in {
      val tempFile = File.createTempFile("test", ".txt")
      tempFile.deleteOnExit()
      val url = tempFile.toURI.toURL.toString

      intercept[Exception] {
        handler.load(url)
      }
    }
  }
}
