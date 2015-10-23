package shared

case class Hangman(level: Int,
                   word: String,
                   guess: List[Char] = Nil,
                   misses: Int = 0) {

  lazy val guessWord =
    word.toCharArray.map { c => if (guess.contains(c)) c else '_' }.mkString

  def reportStatus = {
    def persuasive(n: Int) = n match {
      case 0 => "no bad guesses at all"
      case 1 => "only one bad guess"
      case _ => s"$n bad guesses"
    }

    s"You have made ${persuasive(misses)} out of a maximum of $level"
  }

  def isEndOfGame = (misses >= level) || isWon

  def isWon = word == guessWord

  /*{
     (for (c <- word.toCharArray) yield {
       guess.contains(c)
     }).find(i => !i ) == None
   }*/
}