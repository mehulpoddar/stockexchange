package com.mehulpoddar.stockexchange.models

case class Company(code:String,
                   name: String,
                   startingPrice: Int,
                   currentPrice: Int,
                   remainingShares: Int) {
  override def toString = s"${name}\nPrice: $currentPrice\nRemaining Shares: $remainingShares\n"

  def updatePrice(players: Seq[Player]): Company = {
    val newPrice = players.foldLeft(currentPrice)((price, player) =>
      price + player.cards.getOrElse(code, Seq(0)).sum)
    copy(currentPrice = if(newPrice > 0) newPrice else 0)
  }

  def processAction(playerInput: PlayerInput): Company = {
    playerInput.action.code match {
      case "b" => copy(remainingShares = remainingShares - playerInput.value)
      case "s" => copy(remainingShares = remainingShares + playerInput.value)
    }
  }
}
