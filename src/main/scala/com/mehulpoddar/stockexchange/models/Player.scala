package com.mehulpoddar.stockexchange.models

import com.mehulpoddar.stockexchange.constants.GameConstants.actionCode

case class Player(code: Int,
                  name: String,
                  cashInHand: Int,
                  actionState: Map[String, Seq[String]] = Map.empty[String, Seq[String]],
                  allottedShares: Map[String, Int] = Map.empty[String, Int],
                  indexes: Map[String, Seq[Int]] = Map.empty[String, Seq[Int]]) {

  override def toString = s"${name}\nCash: $cashInHand\nShares: ${allottedShares.mkString(", ")}\n"

  def organizeCards(playerCards: Seq[Card]): Player = {
    val indexActionCards = playerCards.foldLeft((Map.empty[String, Seq[Int]], Map.empty[String, Seq[String]]))((map, card) => {
      card match {
        case i: Index =>
          val mapValue = map._1.getOrElse (i.companyCode, Seq.empty[Int])
          (map._1 + (i.companyCode -> (mapValue ++ Seq (i.value))), map._2)
        case a: Action =>
          val mapValue = map._2.getOrElse (a.phase, Seq.empty[String])
          (map._1, map._2 + (a.phase -> (mapValue ++ Seq (a.code))))
      }
    })

    copy(indexes = indexActionCards._1, actionState = indexActionCards._2)
  }

  def processAction(playerInput: PlayerInput, companies: Map[String, Company]): Player = {
    val updatedCash = playerInput.action.code match {
      case actionCode.BUY => cashInHand - (companies (playerInput.companyCode).currentPrice * playerInput.value)
      case actionCode.SELL => cashInHand + (companies (playerInput.companyCode).currentPrice * playerInput.value)
    }

    val updatedShares = playerInput.action.code match {
      case actionCode.BUY => allottedShares + (playerInput.companyCode -> (allottedShares.getOrElse(playerInput.companyCode, 0) + playerInput.value))
      case actionCode.SELL => allottedShares + (playerInput.companyCode -> (allottedShares(playerInput.companyCode) - playerInput.value))
    }
    val finalUpdatedShares =
      if(updatedShares(playerInput.companyCode) == 0) updatedShares - playerInput.companyCode else updatedShares

    copy(cashInHand = updatedCash, allottedShares = finalUpdatedShares)
  }

  def netWorth(companies: Map[String, Company]): Int =
     allottedShares.foldLeft(cashInHand)((worth, kv) =>
       worth + (kv._2 * companies.get(kv._1).get.currentPrice))

  def showCards(companies: Map[String, Company]): String =
    indexes.foldLeft("")((str, kv) => str + s"${companies(kv._1).name} (${kv._1}) -> ${kv._2.mkString(", ")} (${kv._2.sum})\n")
}
