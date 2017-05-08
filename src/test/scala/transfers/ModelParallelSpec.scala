package transfers

import org.scalatest.{ WordSpec, Matchers, ParallelTestExecution }

class ModelParallelSpec extends WordSpec with Matchers with ParallelTestExecution {
  "Model" should {
    val model = new Model
    model.addUser(1L)
    model.deposit(1L, 1000)

    "provide safe transfer operation" which {
      model.addUser(2L)
      "moves funds from one user to another" in {
        val before = model.getFunds(1L)
        val numThreads = 10
        val threads = for (t <- 0 until numThreads) yield new Thread {
          override def run() {
            model.transfer(1L, 2L, 10)
          }
        }
        for (t <- threads) t.start()
        for (t <- threads) t.join()
        assert(model.getFunds(1L) == before - 10 * numThreads)
        assert(model.getFunds(2L) == 10 * numThreads)
      }
    }
  }
}
