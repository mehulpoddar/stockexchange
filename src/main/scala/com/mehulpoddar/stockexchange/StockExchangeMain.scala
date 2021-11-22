package com.mehulpoddar.stockexchange

import com.mehulpoddar.stockexchange.constants.GameConstants._
import com.mehulpoddar.stockexchange.models.GameSettings
import com.mehulpoddar.stockexchange.service.{GameService, Gameplay}

object Main extends App with GameService with Gameplay {

  val gameSettings = GameSettings(
    noOfRounds,
    noOfTurns,
    initCashInHand,
    initCompanyShares,
    cardsPerPlayerPerRound,
    actionsSeq,
    companyDetails,
    playerDetails
  )

  val board = setup(gameSettings)
  val finalBoard = startGame(board)
  complete(finalBoard)
}
