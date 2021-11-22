package com.mehulpoddar.stockexchange.models

import com.mehulpoddar.stockexchange.service.Board

case class GameSettings(rounds: Int,
                        turns: Int,
                        initCashInHand: Double,
                        initCompanyShares: Int,
                        cardsPerPlayerPerRound: Int,
                        actions: Seq[Action],
                        companyDetails: Seq[CompanyDetails],
                        playerDetails: Seq[String])

case class CompanyDetails(code: String, name: String, startPrice: Double, cardDetails: Seq[(Double, Int)])
// cardDetails = Seq( (card value 1, count of card value 1), ...)

case class Card(companyCode: String, companyName: String, value: Double)

case class Action(code: String, name: String, display: String)

case class PlayerInput(playerId: Int, action: Action, companyCode: String, value: Int) {

  def displayText(board: Board) = {
    action.code match {
      case "b" | "s" => s"${board.players(playerId).name} ${action.display} " +
      s"$value shares of ${board.companies(companyCode).name}"
      case "p" => s"${board.players(playerId).name} ${action.display}"
    }
  }
}
// Input in single line as - action companyCode value

case class HistoryEntry(round: Int, players: Seq[Player], companies: Map[String, Company]) {
  override def toString: String =
    s"\n${"-" * 10} Round $round ${"-" * 10}\n" +
      players.foldLeft("")((str, p) => str + s"${p.name}: ${p.netWorth(companies)}\n") +
      companies.foldLeft("")((str, kv) => str + s"\n${kv._2.name}: ${kv._2.currentPrice}") +
      s"\n${"-" * 30}"
}