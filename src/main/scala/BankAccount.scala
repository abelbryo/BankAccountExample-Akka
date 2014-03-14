import akka.actor._
import scala.concurrent.duration._

object BankAccount {

  case class Deposit(amount: BigInt){
    require(amount > 0)
  }

  case class Withdraw(amount: BigInt){
    require(amount > 0)
  }

  case object GetBalance
  case object Fail
  case object Done
}

class BankAccount extends Actor {
  var balance: BigInt = 0;
  def receive = {
    case BankAccount.Deposit(amount) => balance += amount
                                        sender ! BankAccount.Done
    case BankAccount.Withdraw(amount) if amount <= balance => balance -= amount
                                                              sender ! BankAccount.Done
    case BankAccount.GetBalance => sender ! balance
    case _ => sender ! BankAccount.Fail
  }
}
