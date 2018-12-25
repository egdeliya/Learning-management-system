package Domain.Slick.tables

import slick.jdbc.H2Profile.api._


package object Profiles {

  class Profiles(tag: Tag)
    extends Table[(Long, String, String, String, String, String, String, String)](tag, "PROFILES") {

    def id: Rep[Long] = column[Long]("ID", O.PrimaryKey)
    def phone: Rep[String] = column[String]("PHONE")
    def homeTown: Rep[String] = column[String]("HOME_TOWN")
    def desc: Rep[String] = column[String]("DESC")
    def vkLink: Rep[String] = column[String]("VK")
    def fbLink: Rep[String] = column[String]("FB")
    def lnLink: Rep[String] = column[String]("LN")
    def instLink: Rep[String] = column[String]("INST")

    def idx = index("idx_profiles", id, unique = true)
    def * = (id, phone, homeTown, desc, vkLink, fbLink, lnLink, instLink)
  }

  lazy val profiles = TableQuery[Profiles]
}
