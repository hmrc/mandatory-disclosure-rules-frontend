package viewmodels

trait Section

case class AnswerSection(headingKey: Option[String], rows: Seq[GenericAnswerRow]) extends Section
