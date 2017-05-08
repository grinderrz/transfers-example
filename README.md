# transfers-example

This is example application implementing simple RESTful servie for money transfer.  
It is built using akka-http and scala-stm.  

Download release https://github.com/grinderrz/transfers-example/releases/tag/0.1  
and run it:
```
> java -jar transfers-assembly-0.1.jar
```

or build it manually and run:
```
> sbt assembly
> java -jar target/scala-2.12/transfers-assembly-0.1.jar
```

Some possible improvements:
* Make more tests for concurrency (though not necessary if we rely on scala-stm and trust it).
* Maintain transaction log for each account. Store logs in `TMap[Long, Transaction]`. Where Transaction is `sealed trait Transaction { def id: Long }` with some concrete members `case class Deposit(id: Long, amount: Int)`, `case class Withdraw(id: Long, amount: Int)`, `case class Transfer(id: Long, to: Long, amount: Int)`. Maybe extend it to actual Event Sourcing.
* Add getHistory model method and route to request account history
* Make all mutation routes return json representations of created transaction
* Add timestamp to transactions and implement getHistory by time interval
* Etc.
