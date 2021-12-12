package com.mehulpoddar.stockexchange.models

import com.mehulpoddar.stockexchange.constants.GameConstants.actionCode
import com.mehulpoddar.stockexchange.service.Board

case class GameSettings(rounds: Int,
                        turns: Int,
                        initCashInHand: Int,
                        initCompanyShares: Int,
                        cardsPerPlayerPerRound: Int,
                        actions: Seq[Action],
                        defaultPlayerActions: Seq[String],
                        companyDetails: Seq[CompanyDetails],
                        playerDetails: Seq[String])

case class CompanyDetails(code: String, name: String, startPrice: Int, cardDetails: Seq[(Int, Int)])
// cardDetails = Seq( (card value 1, count of card value 1), ...)

trait Card
case class Index(companyCode: String, companyName: String, value: Int) extends Card
case class Action(code: String, name: String, display: String, phase: String) extends Card

case class PlayerInput(playerId: Int, action: Action, companyCode: String, value: Int) {

  def displayText(board: Board) = {
    action.code match {
      case actionCode.BUY | actionCode.SELL => s"${board.players(playerId).name} ${action.display} " +
      s"$value share(s) of ${board.companies(companyCode).name}"
      case actionCode.PASS => s"${board.players(playerId).name} ${action.display}"
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