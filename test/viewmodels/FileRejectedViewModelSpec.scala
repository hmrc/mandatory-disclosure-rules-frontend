package viewmodels

import base.SpecBase
import models.fileDetails._
import uk.gov.hmrc.govukfrontend.views.Aliases.{TableRow, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table}

class FileRejectedViewModelSpec extends SpecBase {

  val header = Some(
    List(
      table.HeadCell(Text("Code"), None, "", None, None, Map()),
      HeadCell(Text("DocRefId"), None, "govuk-!-width-one-half", None, None, Map()),
      HeadCell(Text("Error"), None, "govuk-!-width-one-half", None, None, Map())
    )
  )

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
          header,
          Some("Errors"),
          "govuk-table__caption govuk-heading-m",
          false,
          "",
          Map()
        )

      FileRejectedViewModel.createTable(validationErrors)(messages(app)) mustBe expectedTable

    }

    "create table for all the allowed the custom errors " in {

      val recordErrors = Some(
        Seq(
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_901), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_902), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_903), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_904), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_905), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_906), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_907), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_908), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_909), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_910), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_911), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_912), None)
        )
      )

      val expectedTable = Table(
        List(
          List(
            TableRow(Text("901"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(
              Text(
                "The CorrDocRefId does not match a DocRefId from the same type of section (either Disclosing or MdrReport). It must refer to the same element"
              ),
              None,
              "",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("902"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(Text("The MdrReport CorrDocRefId does not refer to the same previously sent MdrBody as the Disclosing element"),
                     None,
                     "",
                     None,
                     None,
                     Map()
            )
          ),
          List(
            TableRow(Text("903"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(Text("The Disclosing section contains resent data (DocTypeIndic = OECD0) so it must not have a CorrDocRefId"), None, "", None, None, Map())
          ),
          List(
            TableRow(Text("904"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(Text("Disclosing Capacity is not one of the allowed values for the MdrReport CrsAvoidance or OOS Reason provided"),
                     None,
                     "",
                     None,
                     None,
                     Map()
            )
          ),
          List(
            TableRow(Text("905"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(Text("Since the DocTypeIndic of Disclosing is OECD0, the DocTypeIndic of MdrReport must be OECD2"), None, "", None, None, Map())
          ),
          List(
            TableRow(Text("906"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(
              Text("Since the MdrReport has a DocTypeIndic of OECD3, indicating this section must be deleted, this Disclosing section must be deleted too"),
              None,
              "",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("907"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(
              Text(
                "Since the MessageTypeIndic contains the value of MDR401 for new information, the Disclosing DocTypeIndic must contain the value of OECD1 for new information"
              ),
              None,
              "",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("908"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(
              Text(
                "Since the MessageTypeIndic contains the value of MDR401 for new information, an MdrReport section must be provided with a DocTypeIndic of OECD1 for new information"
              ),
              None,
              "",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("909"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(
              Text("DocRefId must be 100 characters or less, start with your 15-character MDR ID and include up to 85 other characters of your choice"),
              None,
              "",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("910"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(
              Text("MessageRefId must be 85 characters or less, start with your 15-character MDR ID and include up to 70 other characters of your choice"),
              None,
              "",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("911"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(Text("Provide an issuedBy for every TIN that has a value other than NOTIN"), None, "", None, None, Map())
          ),
          List(
            TableRow(Text("912"), None, "", None, None, Map()),
            TableRow(HtmlContent(""), None, "", None, None, Map()),
            TableRow(Text("The top level of the StructureChart must not have an Ownership or InvestAmount"), None, "", None, None, Map())
          )
        ),
        header,
        Some("Errors"),
        "govuk-table__caption govuk-heading-m",
        false,
        "",
        Map()
      )

      val validationErrors = ValidationErrors(None, recordErrors)

      FileRejectedViewModel.createTable(validationErrors)(messages(app)) mustBe expectedTable

    }

  }

}
