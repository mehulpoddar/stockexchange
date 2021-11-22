package com.mehulpoddar.stockexchange.constants

import com.mehulpoddar.stockexchange.models.{Action, CompanyDetails}

object GameConstants {
  val noOfRounds             = 10
  val noOfTurns              = 3
  val initCashInHand         = 600000
  val initCompanyShares      = 200000
  val cardsPerPlayerPerRound = 10

  val gsCards = Seq((10, 2), (5, 2), (-10, 2), (-5, 2))
  val tsCards = Seq((15, 2), (10, 2), (5, 2), (-15, 2), (-10, 2), (-5, 2))
  val rlCards = Seq((15, 2), (10, 2), (5, 2), (-15, 2), (-10, 2), (-5, 2))
  val clCards = Seq((20, 1), (15, 2), (10, 2), (5, 2), (-20, 1), (-15, 2), (-10, 2), (-5, 2))
  val csCards = Seq((25, 1), (20, 2), (15, 2), (10, 2), (5, 2), (-25, 1), (-20, 2), (-15, 2), (-10, 2), (-5, 2))
  val ihCards = Seq((30, 1), (25, 1), (20, 2), (15, 2), (10, 2), (5, 2), (-30, 1), (-25, 1), (-20, 2), (-15, 2), (-10, 2), (-5, 2))

  val playerDetails  = Seq("Mihir", "Mehul", "Suresh")
  val companyDetails = Seq(
    CompanyDetails("gs", "G.E. Shipping", 20, gsCards),
    CompanyDetails("ts", "TISCO", 25, tsCards),
    CompanyDetails("rl", "Reliance", 40, rlCards),
    CompanyDetails("cl", "Colgate", 55, clCards),
    CompanyDetails("cs", "Castrol", 75, csCards),
    CompanyDetails("ih", "Indian Hotels", 80, ihCards),
  )

  val actionsSeq = Seq(
    Action("b", "buy", "bought"),
    Action("s", "sell", "sold"),
    Action("p", "pass", "passed")
  )

  def line(ch: String, count: Int) = println(s"${ch * count}")
  def textLine(text: String, ch: String, count: Int) = println(s"${ch * count} $text ${ch * count}")
}
