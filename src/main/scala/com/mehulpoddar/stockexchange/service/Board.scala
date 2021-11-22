package com.mehulpoddar.stockexchange.service

import com.mehulpoddar.stockexchange.models._
import com.mehulpoddar.stockexchange.constants.GameConstants.{line, textLine}

import scala.io.StdIn.readLine
import scala.util.Try

case class Board(news: Seq[String],
                 companies: Map[String, Company],
                 players: Seq[Player],
                 cardSet: Seq[Card],
                 actions: Map[String, Action],
                 settings: GameSettings,
                 activePlayerId: Int = -1,
                 history: Seq[HistoryEntry] = Seq.empty) {

  def getPlayerInput: PlayerInput = {
    val input = readLine(s"\n${players(activePlayerId).name}, enter your move: ").split(" ")
    val (action, companyCode, value) = (Try(input(0)), Try(input(1)), Try(input(2).toInt))

    val validInput =
      action.isSuccess && actions.contains(action.get) &&
        (action.get == "p" ||
        (companyCode.isSuccess && value.isSuccess &&
        companies.contains(companyCode.get) &&
        value.get > 0 && value.get <= settings.initCompanyShares))

    val conditionalValidation = validInput && (action.get match {
      case "b" => value.get <= companies(companyCode.get).remainingShares &&
        players(activePlayerId).cashInHand >= (companies(companyCode.get).currentPrice * value.get)
      case "s" => players(activePlayerId).allottedShares(companyCode.get) >= value.get
      case "p" => true
    })

    if(conditionalValidation) {
      PlayerInput(activePlayerId, actions(action.get), companyCode.getOrElse(""), value.getOrElse(0))
    } else {
      println("Invalid input, try again.")
      getPlayerInput
    }
  }

  def clearNews = copy(news = Seq.empty[String])
  def addNews(newNews: Seq[String]) = copy(news = news ++ newNews)

  def addToHistory(roundNo: Int) =
    copy(history = history ++ Seq(HistoryEntry(roundNo, players, companies)))

  def displayBoard(showCards: Boolean = false): Unit = {
    line("\n", 30)

    textLine("Game Board", "=", 10)
    println()

    textLine("News", "-", 10)
    news.foreach(n => println(n))
    line("-", 30)
    println()

    textLine("Company Stats", "-", 10)
    companies.foreach(kv => println(kv._2))
    line("-", 30)
    println()

    textLine("Player Stats", "-", 10)
    players.foreach(p => println(p))
    line("-", 30)
    println()

    if(showCards) {
      val currentPlayer = players(activePlayerId)
      textLine(s"${currentPlayer.name}'s cards", "-", 10)
      println(currentPlayer.showCards(companies))
      line("-", 30)
      println()
    }

    line("=", 30)
    println()
  }

  def showHistory: Unit = {
    println()
    textLine("Game History", "=", 10)
    history.foreach(h => println(h))
    line("=", 30)
    println()
  }

  def showRanking: Unit = {
    val sortedPlayers = players.sortBy(_.netWorth(companies)).reverse
    println()
    textLine("Player Ranking", "=", 10)
    sortedPlayers.foreach(p => println(s"${p.name} --- ${p.netWorth(companies)}"))
    line("=", 30)
    println()
  }
}
