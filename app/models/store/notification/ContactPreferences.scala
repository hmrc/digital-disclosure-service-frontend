package models.store.notification

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Json, OFormat, Reads, Writes}


sealed trait Preference
object Preference {
  implicit val reads: Reads[Preference] = Reads {
    case JsString("Email") => JsSuccess(Email)
    case JsString("Telephone") => JsSuccess(Telephone)
    case _ => JsError("error.invalid")
  }

  implicit val writes = Writes[Entity] {
    case Individual => Json.toJson("Email")
    case Estate => Json.toJson("Email")
  }

  implicit val format: Format[Preference] =  Format(reads, writes)
}

case object Email extends Preference
case object Telephone extends Preference

final case class ContactPreferences(preferences: Set[Preference])

object ContactPreferences {
  implicit val format: OFormat[ContactPreferences] = Json.format[ContactPreferences]
}
