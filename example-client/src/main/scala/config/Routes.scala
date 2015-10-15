package config

object Routes {

  object Todos {
    val base = "/todos"
    def all = base + "/all"
    def create = base + "/create"
    def update(id: Long) =  s"$base/update/$id"
    def delete(id: Long) =  s"$base/delete/$id"
    def clear = base + "/clear"
  }

  object Hangman {
    val base = "/hangman"
    def start(level: Int) = s"$base/start/$level"
    def session = base + "/session"
    def guess(g: Char) = s"$base/guess/$g"
    def giveup = base + "/giveup"
  }

  object Chat {
    val base = "/chat"
    def connectSSE(username: String) = s"$base/sse/$username"
    def talk = base + "/talk"
  }
}
