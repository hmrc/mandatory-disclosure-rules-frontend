package viewmodels

trait GenericAnswerRow {
  def label: String
  def answer: String
  def answerIsMessageKey: Boolean
  def changeUrl: String
  def hiddenText: Option[String]
  def labelArgs: Seq[String]
  def hiddenTextArgs: Seq[String]
}

case class AnswerRow(
                      label: String,
                      answer: String,
                      answerIsMessageKey: Boolean,
                      changeUrl: String,
                      hiddenText: Option[String] = None,
                      labelArgs: Seq[String] = Nil,
                      hiddenTextArgs: Seq[String] = Nil
                    ) extends GenericAnswerRow