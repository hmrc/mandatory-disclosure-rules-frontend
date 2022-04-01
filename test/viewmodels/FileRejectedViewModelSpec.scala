package viewmodels

import base.SpecBase
import models.fileDetails._
import uk.gov.hmrc.govukfrontend.views.Aliases.{TableRow, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table}

class FileRejectedViewModelSpec extends SpecBase {

  "FileRejectedViewModel" - {
    "create table for all the allowed error codes except custom errors " in {

      val fileErrors =
        Some(Seq(FileErrors(FileErrorCode.MessageRefIDHasAlreadyBeenUsed, None), FileErrors(FileErrorCode.FileContainsTestDataForProductionEnvironment, None)))

      val recordErrors = Some(
        Seq(
          RecordError(RecordErrorCode.DocRefIDAlreadyUsed, None, None),
          RecordError(RecordErrorCode.CorrDocRefIdUnknown, None, None),
          RecordError(RecordErrorCode.CorrDocRefIdNoLongerValid, None, None),
          RecordError(RecordErrorCode.CorrDocRefIdForNewData, None, None),
          RecordError(RecordErrorCode.MissingCorrDocRefId, None, None),
          RecordError(RecordErrorCode.ResendOption, None, None),
          RecordError(RecordErrorCode.DeleteParentRecord, None, None),
          RecordError(RecordErrorCode.MessageTypeIndic, None, None),
          RecordError(RecordErrorCode.CorrDocRefIDTwiceInSameMessage, None, None),
          RecordError(RecordErrorCode.UnknownDocRefID, None, None),
          RecordError(RecordErrorCode.DocRefIDIsNoLongerValid, None, None)
        )
      )
      val validationErrors = ValidationErrors(fileErrors, recordErrors)

      val expectedTable =
        Table(
          List(
            List(
              TableRow(Text("509"), None, "", None, None, Map()),
              TableRow(Text("File"), None, "", None, None, Map()),
              TableRow(Text("Please replace the MessageRefID field value with a unique value (not containing all blanks), and resubmit the file"),
                       None,
                       "",
                       None,
                       None,
                       Map()
              )
            ),
            List(
              TableRow(Text("510"), None, "", None, None, Map()),
              TableRow(Text("File"), None, "", None, None, Map()),
              TableRow(Text("We cannot accept test data so each DocTypeIndic must have a value of either OECD0, OECD1, OECD2 or OECD3"),
                       None,
                       "",
                       None,
                       None,
                       Map()
              )
            ),
            List(
              TableRow(Text("800"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(Text("The DocRefId has already been used in this file or a file previously received, it must be unique"), None, "", None, None, Map())
            ),
            List(
              TableRow(Text("802"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(Text("The CorrDocRefId does not match any DocRefId previously received"), None, "", None, None, Map())
            ),
            List(
              TableRow(Text("803"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(Text("The CorrDocRefId is for a section that has already been corrected or deleted"), None, "", None, None, Map())
            ),
            List(
              TableRow(Text("804"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(Text("Sections that contain new or resent information must not have a CorrDocRefId"), None, "", None, None, Map())
            ),
            List(
              TableRow(Text("805"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(Text("This section contains a correction or deletion so it must contain a CorrDocRefId"), None, "", None, None, Map())
            ),
            List(
              TableRow(Text("808"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(
                Text(
                  "Resend option (OECD0) must only be used for the Disclosing element, not for the MdrReport element. Ensure the MdrReport DocTypeIndic contains one of the allowed values"
                ),
                None,
                "",
                None,
                None,
                Map()
              )
            ),
            List(
              TableRow(Text("809"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(Text("This Disclosing section can only be deleted if the MdrReport section linked to it is also deleted"), None, "", None, None, Map())
            ),
            List(
              TableRow(Text("810"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(
                Text(
                  "The file cannot contain a combination of new information (DocTypeIndic = OECD1) and corrections or deletions (DocTypeIndic = OECD2 or OECD3)"
                ),
                None,
                "",
                None,
                None,
                Map()
              )
            ),
            List(
              TableRow(Text("811"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(Text("A CorrDocRefId value must not be used more than once in the same file"), None, "", None, None, Map())
            ),
            List(
              TableRow(Text("813"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(
                Text(
                  "The Disclosing DocTypeIndic of OECD0 indicates this section contains resent information, but the DocRefId does not match any we have received"
                ),
                None,
                "",
                None,
                None,
                Map()
              )
            ),
            List(
              TableRow(Text("814"), None, "", None, None, Map()),
              TableRow(HtmlContent(""), None, "", None, None, Map()),
              TableRow(
                Text(
                  "The Disclosing DocTypeIndic of OECD0 shows this section contains resent information, but the DocRefId is for information that has since been corrected or deleted. Provide the DocRefId of the section you want to correct"
                ),
                None,
                "",
                None,
                None,
                Map()
              )
            )
          ),
          Some(
            List(
              table.HeadCell(Text("Code"), None, "", None, None, Map()),
              HeadCell(Text("DocRefId"), None, "govuk-!-width-one-half", None, None, Map()),
              HeadCell(Text("Error"), None, "govuk-!-width-one-half", None, None, Map())
            )
          ),
          Some("Errors"),
          "govuk-table__caption govuk-heading-m",
          false,
          "",
          Map()
        )

      FileRejectedViewModel.createTable(validationErrors)(messages(app)) mustBe expectedTable

    }

    "create table for all the allowed the custom errors " in {

      val fileErrors =
        Some(Seq(FileErrors(FileErrorCode.MessageRefIDHasAlreadyBeenUsed, None), FileErrors(FileErrorCode.FileContainsTestDataForProductionEnvironment, None)))

      val recordErrors = Some(
        Seq(
          RecordError(RecordErrorCode.CustomError,
            Some("The CorrDocRefId does not match a DocRefId from the same type of section (either Disclosing or MdrReport). It must refer to the same element"), None),
          RecordError(RecordErrorCode.CustomError, Some(""), None),
          RecordError(RecordErrorCode.CustomError, Some(""), None),
          RecordError(RecordErrorCode.CustomError, Some(""), None),
          RecordError(RecordErrorCode.CustomError, Some(""), None)
        )
      )
      val validationErrors = ValidationErrors(fileErrors, recordErrors)

      FileRejectedViewModel.createTable(validationErrors)(messages(app)) mustBe ""

    }

  }

}
