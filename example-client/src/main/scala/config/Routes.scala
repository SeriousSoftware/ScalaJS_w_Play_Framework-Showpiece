package config

object Routes {

  object Todos {
    val base = "/todos"
    val all = base + "/all"
    val create = base + "/create"
    val clear = base + "/clear"
    def update(id: Long) = s"$base/update/$id"
    def delete(id: Long) = s"$base/delete/$id"
  }

  object Hangman {
    val base = "/hangman"
    val session = base + "/session"
    val giveup = base + "/giveup"
    def start(level: Int) = s"$base/start/$level"
    def guess(g: Char) = s"$base/guess/$g"
  }

  object Chat {
    val base = "/chat"
    val talk = base + "/talk"
    def connectSSE(username: String) = s"$base/sse/$username"
  }

}
