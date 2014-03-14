import akka.actor._
import scala.concurrent.duration._
import akka.event.LoggingReceive

class TransferMain extends Actor {
  val accountA = context.actorOf(Props[BankAccount], "account-alice")
  val accountB = context.actorOf(Props[BankAccount], "account-bob")

  accountA ! BankAccount.Deposit(100)

  def receive = LoggingReceive{
    case BankAccount.Done => 
      transfer(50)
  }

  def transfer(amount: BigInt): Unit = {
    val transaction = context.actorOf(Props[WireTransfer], "transfer")

    transaction ! WireTransfer.Transfer(accountA, accountB, amount)
    context.become(LoggingReceive{
      case WireTransfer.Done =>
        println("Success")
        context.stop(self)
    })
  }
}

