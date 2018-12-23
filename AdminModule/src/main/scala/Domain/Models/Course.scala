package Domain.Models

case class Course(name: String,
                  description: String) {
  require(name.nonEmpty, "course name must be non empty!")
  require(description.nonEmpty, "course description must be non empty!")
}
