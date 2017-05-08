package transfers

import org.scalatest.{ WordSpec, Matchers }

class ModelSpec extends WordSpec with Matchers {
  "Model" should {
    val model = new Model

    "provide addUser operation" which {
      "creates new user" in {
        model.addUser(1L)
      }
      "denies creation" when {
        "user already exists" in {
          assertThrows[UserExistsException] {
            model.addUser(1L)
          }
        }
      }
    }

    "provide getFunds operation" which {
      "returns users funds" in {
        assert(model.getFunds(1L) == 0)
      }
      "denies" when {
        "there is no such user" in {
          assertThrows[NoSuchUserException] {
            model.getFunds(2L)
          }
        }
      }
    }

    "provide deposit operation" which {
      "increases users funds" in {
        val before = model.getFunds(1L)
        model.deposit(1L, 10)
        assert(model.getFunds(1L) == before + 10)
      }
      "denies" when {
        "there is no such user" in {
          assertThrows[NoSuchUserException] {
            model.deposit(2L, 10)
          }
        }
      }
    }

    "provide withdraw operation" which {
      "decreases users funds" in {
        val before = model.getFunds(1L)
        model.withdraw(1L, 5)
        assert(model.getFunds(1L) == before - 5)
      }
      "denies" when {
        "there is no such user" in {
          assertThrows[NoSuchUserException] {
            model.withdraw(2L, 10)
          }
        }
      }
      "denies" when {
        "there is not enough money in users funds" in {
          val before = model.getFunds(1L)
          assertThrows[InsufficientFundsException] {
            model.withdraw(1L, before + 5)
          }
        }
      }
    }

    "provide transfer operation" which {
      model.addUser(3L)
      "moves funds from one user to another" in {
        val before = model.getFunds(1L)
        model.transfer(1L, 3L, 1)
        assert(model.getFunds(1L) == before - 1)
        assert(model.getFunds(3L) == 1)
      }
      "denies" when {
        "there is no such sorce user" in {
          assertThrows[NoSuchUserException] {
            model.transfer(2L, 3L, 1)
          }
        }
      }
      "denies" when {
        "there is no such destination user" in {
          assertThrows[NoSuchUserException] {
            model.transfer(1L, 2L, 1)
          }
        }
      }
      "denies" when {
        "there is not enough money in source users funds" in {
          val before = model.getFunds(1L)
          assertThrows[InsufficientFundsException] {
            model.transfer(1L, 3L, before + 1)
          }
        }
      }
    }
  }
}
