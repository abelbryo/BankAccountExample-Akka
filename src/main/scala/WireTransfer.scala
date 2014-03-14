import akka.actor._
import scala.concurrent.duration._

object WireTransfer {
  case class Transfer(from: ActorRef, to:ActorRef, amount: BigInt)
  case object Fail
  case object Done
}

class WireTransfer extends Actor {

  def receive = {
    case WireTransfer.Transfer(from, to, amount) => {
      from ! BankAccount.Withdraw(amount)
      context.become(waitUntilWithdrawCompletion(to, amount, sender))
    }
  }

  def waitUntilWithdrawCompletion(to: ActorRef, amount: BigInt, client: ActorRef): Receive= {
    case BankAccount.Done =>
      to ! BankAccount.Deposit(amount)
      context.become(waitUntilDepositCompletion(client))
    case BankAccount.Fail =>
      client ! WireTransfer.Fail
      context.stop(self)
  }

  def waitUntilDepositCompletion(client: ActorRef): Receive = {
    case BankAccount.Done =>
      client ! WireTransfer.Done
      context.stop(self)
  }

}
