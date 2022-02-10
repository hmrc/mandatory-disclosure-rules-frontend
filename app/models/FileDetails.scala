package models

import play.api.libs.json.{Json, OFormat}

  import java.time.LocalDateTime

sealed trait FileStatus
case object Pending extends FileStatus
case class Rejected(error:FileError) extends FileStatus
case object Accepted extends FileStatus

case class FileError(detail:String)

object FileError {
  implicit val format: OFormat[FileError] = Json.format[FileError]
}


case class FileDetails(name:String, submitted:LocalDateTime, status: FileStatus, conversationId:String)

object FileDetails{
  implicit val format: OFormat[FileDetails] = Json.format[FileDetails]
}


