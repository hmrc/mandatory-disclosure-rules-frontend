/*
 * Copyright 2025 HM Revenue & Customs
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

import base.SpecBase
import org.scalatest.freespec.AnyFreeSpec
import play.api.Configuration

class ServiceSpec extends SpecBase {

  "Service" - {

    "baseUrl must combine protocol, host and port" in {
      val service = Service("localhost", "8080", "http")
      service.baseUrl mustEqual "http://localhost:8080"
    }

    "toString must return baseUrl" in {
      val service = Service("127.0.0.1", "9000", "https")
      service.toString mustEqual "https://127.0.0.1:9000"
    }

    "implicit conversion to String must return baseUrl" in {
      import Service.convertToString

      val service: Service = Service("example.com", "443", "https")
      val str: String      = service // uses implicit
      str mustEqual "https://example.com:443"
    }

    "ConfigLoader must load a Service from a Configuration" in {
      val conf = Configuration(
        "my-service.host"     -> "localhost",
        "my-service.port"     -> "8080",
        "my-service.protocol" -> "http"
      )

      val loader  = implicitly[play.api.ConfigLoader[Service]]
      val service = loader.load(conf.underlying, "my-service")

      service mustEqual Service("localhost", "8080", "http")
    }

    "ConfigLoader must throw an exception if any field is missing" in {
      val conf = Configuration(
        "my-service.host" -> "localhost",
        "my-service.port" -> "8080"
        // protocol is missing
      )

      val loader = implicitly[play.api.ConfigLoader[Service]]

      intercept[com.typesafe.config.ConfigException.Missing] {
        loader.load(conf.underlying, "my-service")
      }
    }
  }
}
