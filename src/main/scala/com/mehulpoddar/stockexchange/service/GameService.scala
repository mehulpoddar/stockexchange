package com.mehulpoddar.stockexchange.service

import com.mehulpoddar.stockexchange.models.{Action, Card, Company, GameSettings, Player, PlayerInput}

import scala.util.Random

trait GameService {
  def setup(gs: GameSettings): Board = {
    val players = gs.playerDetails.zipWithIndex.map(nameCode => Player(nameCode._2, nameCode._1, gs.initCashInHand, Map.empty ))
    val companies = gs.companyDetails.foldLeft(Map.empty[String, Company]) { (map, c) =>
      map + (c.code -> Company(c.code, c.name, c.startPrice, c.startPrice, gs.initCompanyShares))
    }
    val cardSet = gs.companyDetails.foldLeft(Seq.empty[Card]) { (seq, c) =>
      seq ++ c.cardDetails.foldLeft(Seq.empty[Card]) { (cseq, d) => cseq ++ Seq.fill(d._2)(Card(c.code, c.name, d._1)) }
    }
    val actionsMap = gs.actions.foldLeft(Map.empty[String, Action])((map, a) => map + (a.code -> a))
    Board(Seq("Welcome to StockExchange!"), companies, players, cardSet, actionsMap, gs)
  }

  def distributeCards(board: Board): Board = {
    val shuffledCardSet = Random.shuffle(board.cardSet)
    val playersWithCards = Seq.tabulate(board.players.size)(i => {
      val numCards = board.settings.cardsPerPlayerPerRound
      board.players(i).organizeCards(shuffledCardSet.slice(numCards * i, numCards * (i + 1)))
    })
    board.copy(players = playersWithCards)
  }

  def evaluateCards(board: Board): Board = {
    val updatedCompanies = board.companies.foldLeft(Map.empty[String, Company])((map, kv) =>
      map + (kv._1 -> kv._2.updatePrice(board.players)))
    val news = updatedCompanies.foldLeft(Seq.empty[String])((seq, kv) =>
      seq ++ Seq(s"${kv._2.name} price changed from ${board.companies(kv._1).currentPrice} to ${kv._2.currentPrice}"))
    board.copy(companies = updatedCompanies).clearNews.addNews(news)
  }

  def processMove(board: Board, playerInput: PlayerInput): Board = {
    val updatedPlayer = board.players(playerInput.playerId).processAction(playerInput, board.companies)
    val updatedPlayers = board.players.updated(playerInput.playerId, updatedPlayer)

    val updatedCompanies = board.companies +
      (playerInput.companyCode -> board.companies(playerInput.companyCode).processAction(playerInput))

    board.copy(players = updatedPlayers, companies = updatedCompanies)
  }

  def complete(board: Board): Unit = {
    board.showRanking
  }
}
