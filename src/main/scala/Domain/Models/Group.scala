package Domain.Models

case class Group(groupName: String,
                 facultyName: String,
                 courseNum: Long) {
  require(groupName.nonEmpty, "group name must be non empty!")
  require(facultyName.nonEmpty, "faculty name must be non empty!")
  require(courseNum > 0 && courseNum < 7, "course number must be positive and less than 7!")
}
