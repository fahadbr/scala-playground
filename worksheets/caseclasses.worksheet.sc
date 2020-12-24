
case class JsObject(values: Map[String, Any])

object JsObject {
  def apply(values: (String, Any)*): JsObject = JsObject(values.toMap)
}

JsObject(
  "hi" -> 1,
  "hello" -> 2,
  "hello" -> 3
)
