package Domain.Models

case class Course(courseName: String,
                  courseDescription: String) {
  require(courseName.nonEmpty, "course name must be non empty!")
  require(courseDescription.nonEmpty, "course description must be non empty!")
}
