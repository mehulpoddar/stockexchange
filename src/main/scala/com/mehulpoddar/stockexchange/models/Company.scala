package com.mehulpoddar.stockexchange.models

case class Company(code:String,
                   name: String,
                   startingPrice: Double,
                   currentPrice: Double,
                   remainingShares: Int) {
  override def toString = s"${name}\nPrice: $currentPrice\nRemaining Shares: $remainingShares\n"

  def updatePrice(players: Seq[Player]): Company =
    copy(currentPrice = players.foldLeft(currentPrice)((price, player) =>
      price + player.cards.getOrElse(code, Seq(0.0)).sum))

  def processAction(playerInput: PlayerInput): Company = {
    playerInput.action.code match {
      case "b" => copy(remainingShares = remainingShares - playerInput.value)
      case "s" => copy(remainingShares = remainingShares + playerInput.value)
    }
  }
}
