package transfers

import scala.concurrent.stm.{ TMap, atomic }

sealed trait ModelException extends Exception {
  def message: String
}
case class UserExistsException(message: String = "User already exists.") extends ModelException
case class NoSuchUserException(message: String = "No such user.") extends ModelException
case class InsufficientFundsException(message: String = "Insufficient funds.") extends ModelException

class Model {
  val storage = TMap[Long, Int]()

  def addUser(id: Long) = atomic { implicit txn =>
    if (storage.contains(id)) throw new UserExistsException
    else storage.put(id, 0)
  }

  def getFunds(id: Long) = storage.single.get(id) match {
    case Some(funds) => funds
    case None => throw new NoSuchUserException
  }

  def deposit(id: Long, amount: Int) = atomic { implicit txn =>
    storage.get(id) match {
      case Some(funds) => storage.update(id, funds + amount)
      case None => throw new NoSuchUserException
    }
  }

  def withdraw(id: Long, amount: Int) = atomic { implicit txn =>
    storage.get(id) match {
      case Some(funds) if funds >= amount => storage.update(id, funds - amount)
      case Some(funds) => throw new InsufficientFundsException
      case None => throw new NoSuchUserException
    }
  }

  def transfer(from: Long, to: Long, amount: Int) = atomic { implicit txn =>
    (storage.get(from), storage.get(to)) match {
      case (Some(fromFunds), Some(toFunds)) if fromFunds >= amount => {
        storage.update(from, fromFunds - amount)
        storage.update(to, toFunds + amount)
      }
      case (None, _) => throw new NoSuchUserException
      case (_, None) => throw new NoSuchUserException
      case (Some(fromFunds), Some(toFunds)) => throw new InsufficientFundsException
    }
  }

}
