package Domain.Models

case class User(id: Option[Long] = None,
                name: String,
                surname: String,
                patronymic: String) {
  require(name.nonEmpty, "name must be non empty!")
  require(surname.nonEmpty, "surname must be non empty!")
  require(patronymic.nonEmpty, "patronymic must be non empty!")
}
