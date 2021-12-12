package com.mehulpoddar.stockexchange.service

import com.mehulpoddar.stockexchange.constants.GameConstants.{actionCode, actionPhase}

import scala.annotation.tailrec

trait Gameplay {
  this: GameService =>

  def startGame(board: Board): Board = {
    round(board, board.settings.rounds)
  }

  @tailrec
  private def round(board: Board, currentRound: Int): Board = {
    if(currentRound > 0) {
      val roundNo = board.settings.rounds - currentRound + 1
      val boardWithNews = board.clearNews.addNews(Seq(s"Round $roundNo"))
      val updatedBoard = setupPlayers(boardWithNews)
      val startPlayer = currentRound % updatedBoard.players.size

      val boardInTurn = updatedBoard.copy(gamePhase = actionPhase.IN_TURN)
      val playBoardInTurn = turn(boardInTurn, boardInTurn.settings.turns, startPlayer)

      val boardPreEval = playBoardInTurn.copy(gamePhase = actionPhase.PRE_EVAL)
      val playBoardPreEval = boardPreEval

      val boardEval = evaluateCards(playBoardPreEval).addToHistory(roundNo)

      val boardPostEval = boardEval.copy(gamePhase = actionPhase.POST_EVAL)
      val playBoardPostEval = boardPostEval

      playBoardPostEval.displayBoard()
      playBoardPostEval.showHistory
      round(playBoardPostEval, currentRound - 1)
    } else {
      board
    }
  }

  @tailrec
  private def turn(board: Board, currentTurn: Int, startPlayer: Int): Board = {
    if(currentTurn > 0) {
      val turnNo = board.settings.turns - currentTurn + 1
      val boardWithNews = board.addNews(Seq("", s"Turn $turnNo"))

      val newBoard = playersMoves(boardWithNews, startPlayer, startPlayer)
      turn(newBoard, currentTurn - 1, startPlayer)
    } else board
  }

  @tailrec
  private def playersMoves(board: Board, currentPlayer: Int, startPlayer: Int): Board = {
    val newBoard = board.copy(activePlayerId = currentPlayer)
    newBoard.displayBoard(true)

    val playerInput = newBoard.getPlayerInput
    val updatedBoard = if(playerInput.action.code != actionCode.PASS) processMove(newBoard, playerInput) else newBoard
    val updatedBoardWithNews = updatedBoard.addNews(Seq(s"${playerInput.displayText(updatedBoard)}"))

    val nextPlayer = (currentPlayer + 1) % updatedBoardWithNews.players.size
    if(nextPlayer != startPlayer) playersMoves(updatedBoardWithNews, nextPlayer, startPlayer) else updatedBoardWithNews
  }
}
