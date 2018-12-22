package Domain.Models

import java.util.Date

case class Degree(value: String) {
  private val availableGrades = List("bachelor", "specialist", "magister")
  require(availableGrades.contains(value))
}

case class EducationForm(value: String) {
  private val availableForms = List("full-time", "part-time", "evening")
  require(availableForms.contains(value))
}

case class Basis(value: String) {
  private val availableBasis = List("contract", "budget")
  require(availableBasis.contains(value))
}

case class EntryYear(value: Int) {
  require(value >= 0, "entry year must be positive")
}

case class Student(user: User,
                   group: String,
                   entryYear: EntryYear,
                   grade: Degree,
                   educationForm: EducationForm,
                   basis: Basis) {
  require(group.nonEmpty, "group must be non empty")
}
