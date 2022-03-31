package viewmodels

import base.SpecBase
import models.fileDetails._

class FileRejectedViewModelSpec extends SpecBase {

  "FileRejectedViewModel" - {
    "create table" in {

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
          RecordError(RecordErrorCode.DocRefIDIsNoLongerValid, None, None),
          RecordError(RecordErrorCode.CustomError, None, None),
          RecordError(RecordErrorCode.CustomError, None, None),
          RecordError(RecordErrorCode.CustomError, None, None),
          RecordError(RecordErrorCode.CustomError, None, None)
        )
      )
      val validationErrors = ValidationErrors(fileErrors, recordErrors)

      FileRejectedViewModel.createTable(validationErrors)

    }

  }

}
