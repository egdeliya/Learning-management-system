package Domain.Models

case class Degree(value: String) {
  private val availableDegrees = List("bachelor", "specialist", "magister")
  require(availableDegrees.contains(value), "degree must be one of bachelor, specialist, magister")
}

case class EducationForm(value: String) {
  private val availableForms = List("full-time", "part-time", "evening")
  require(availableForms.contains(value), "form must be one of full-time, part-time, evening")
}

case class Basis(value: String) {
  private val availableBasis = List("contract", "budget")
  require(availableBasis.contains(value), "basis must be one of contract, budget")
}

case class EntryYear(value: Long) {
  require(value >= 0, "entry year must be positive")
}

case class Student(user: User,
                   group: String,
                   entryYear: EntryYear,
                   degree: Degree,
                   educationForm: EducationForm,
                   basis: Basis) {
  require(group.nonEmpty, "group must be non empty")
}
