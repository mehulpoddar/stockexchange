package com.mehulpoddar.stockexchange.models

case class Player(name: String,
                  cashInHand: Double,
                  allottedShares: Map[String, Int],
                  cards: Map[String, Seq[Double]] = Map.empty[String, Seq[Double]]) {

  override def toString = s"${name}\nCash: $cashInHand\nShares: ${allottedShares.mkString(", ")}\n"

  def organizeCards(playerCards: Seq[Card]): Player = {
    val newCards = playerCards.foldLeft(Map.empty[String, Seq[Double]])((map, card) => {
      val mapValue = map.getOrElse(card.companyCode, Seq.empty[Double])
      map + (card.companyCode -> (mapValue ++ Seq(card.value)))
    })
    copy(cards = newCards)
  }

  def processAction(playerInput: PlayerInput, companies: Map[String, Company]): Player = {
    val updatedCash = playerInput.action.code match {
      case "b" => cashInHand - (companies (playerInput.companyCode).currentPrice * playerInput.value)
      case "s" => cashInHand + (companies (playerInput.companyCode).currentPrice * playerInput.value)
    }

    val updatedShares = playerInput.action.code match {
      case "b" => allottedShares + (playerInput.companyCode -> (allottedShares.getOrElse(playerInput.companyCode, 0) + playerInput.value))
      case "s" => allottedShares + (playerInput.companyCode -> (allottedShares(playerInput.companyCode) - playerInput.value))
    }
    val finalUpdatedShares =
      if(updatedShares(playerInput.companyCode) == 0) updatedShares - playerInput.companyCode else updatedShares

    copy(cashInHand = updatedCash, allottedShares = finalUpdatedShares)
  }

  def netWorth(companies: Map[String, Company]): Double =
     allottedShares.foldLeft(cashInHand)((worth, kv) =>
       worth + (kv._2 * companies.get(kv._1).get.currentPrice))

  def showCards(companies: Map[String, Company]): String =
    cards.foldLeft("")((str, kv) => str + s"${companies(kv._1).name} (${kv._1}) -> ${kv._2.mkString(", ")} (${kv._2.sum})\n")
}
