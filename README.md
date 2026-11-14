# Apache Kafka 3.9.2 — Basic Commands

Quick reference for a **Spring Boot Kafka Producer/Consumer** banking practice project.

- **Topic:** `bank-topic`
- **Consumer group:** `bank-group`
- **Broker:** `localhost:9092`
- **Kafka version:** `3.9.2`

> Run these commands from your Kafka installation directory. Linux/macOS commands use `bin/*.sh`. On Windows, use the corresponding `bin\windows\*.bat` scripts.

## 1. Create topic

```bash
bin/kafka-topics.sh --create --topic bank-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

## 2. List topics

```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

## 3. Describe topic

```bash
bin/kafka-topics.sh --describe --topic bank-topic --bootstrap-server localhost:9092
```

## 4. Produce messages manually

```bash
bin/kafka-console-producer.sh --topic bank-topic --bootstrap-server localhost:9092
```

Example message (paste and press Enter):

```json
{"transactionId":"TXN101","accountNumber":"ACC1001","amount":5000,"transactionType":"DEPOSIT"}
```

Press **Ctrl+C** to exit.

## 5. Consume messages from the beginning

```bash
bin/kafka-console-consumer.sh --topic bank-topic --bootstrap-server localhost:9092 --from-beginning
```

## 6. Consume using the Spring Boot consumer group

```bash
bin/kafka-console-consumer.sh --topic bank-topic --bootstrap-server localhost:9092 --group bank-group
```

> Consumers in the **same group share partitions**. Running this console consumer alongside your Spring Boot listener can split messages between them.

## 7. List consumer groups

```bash
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

## 8. Describe consumer group / check lag

```bash
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group bank-group
```

Useful fields:

| Field | Meaning |
|---|---|
| `CURRENT-OFFSET` | Last committed consumer position |
| `LOG-END-OFFSET` | Latest position in the partition |
| `LAG` | Approximate number of records remaining to consume |

## 9. Increase partitions (optional)

```bash
bin/kafka-topics.sh --alter --topic bank-topic --partitions 5 --bootstrap-server localhost:9092
```

> Kafka lets you increase partitions, not decrease them in place. Increasing partitions may change key-to-partition mapping for future messages.

## 10. Show key, partition, and offset while consuming

```bash
bin/kafka-console-consumer.sh --topic bank-topic --bootstrap-server localhost:9092 --from-beginning --property print.key=true --property print.partition=true --property print.offset=true
```

## 11. Check Kafka CLI version

```bash
bin/kafka-topics.sh --version
```

## 12. Delete topic (destructive)

```bash
bin/kafka-topics.sh --delete --topic bank-topic --bootstrap-server localhost:9092
```

## Spring Boot consumer example

```java
@KafkaListener(topics = "bank-topic", groupId = "bank-group")
public void consume(BankTransaction transaction) {
    System.out.println("Received transaction: " + transaction);
}
```

Example `application.properties`:

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=bank-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=com.bank.dto
spring.kafka.consumer.properties.spring.json.value.default.type=com.bank.dto.BankTransaction
```

> Replace `com.bank.dto.BankTransaction` with your actual fully qualified POJO class name. Console-produced JSON does not carry Spring Kafka type headers, so the default type setting helps the consumer deserialize it. For Spring-to-Spring messages, ensure producer type headers and consumer type mappings are compatible.

**Important:** `auto-offset-reset=earliest` applies only when the group has no committed offset for a partition (or its offset is no longer valid). It does not automatically replay previously consumed records.

## Suggested practice flow

1. Start your local Kafka broker.
2. Create `bank-topic` using command 1.
3. Start the Spring Boot consumer with `@KafkaListener`.
4. Start the console producer using command 4 and send a sample JSON transaction.
5. Confirm the Spring Boot consumer logs the transaction.
6. Inspect consumer lag using command 8.

**Reference:** [Apache Kafka 3.9 basic operations](https://kafka.apache.org/39/operations/basic-kafka-operations/)

---

## 13. Start and stop Kafka 3.9.2

Choose **either ZooKeeper mode or KRaft mode**. Do not run both setups for the same local broker. Run commands from the Kafka installation directory.

### Option A — ZooKeeper mode (legacy)

**Terminal 1 — Start ZooKeeper**

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

**Terminal 2 — Start Kafka broker** (after ZooKeeper starts)

```bash
bin/kafka-server-start.sh config/server.properties
```

**Terminal 3 — Create or list your banking topic**

```bash
bin/kafka-topics.sh --create --if-not-exists --topic bank-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

**Stop services** (use separate terminals; stop Kafka first)

```bash
bin/kafka-server-stop.sh
bin/zookeeper-server-stop.sh
```

> ZooKeeper mode is deprecated in Kafka 3.9.2. These commands are for learning or maintaining older setups.

### Option B — KRaft mode (recommended for new local practice)

**One-time setup — Generate a cluster ID**

```bash
bin/kafka-storage.sh random-uuid
```

Copy the generated ID. **Format storage only once for a new cluster** (replace `<CLUSTER_ID>` with the output):

```bash
bin/kafka-storage.sh format -t <CLUSTER_ID> -c config/kraft/server.properties
```

**Terminal 1 — Start Kafka in KRaft mode**

```bash
bin/kafka-server-start.sh config/kraft/server.properties
```

**Terminal 2 — Create and list your topic**

```bash
bin/kafka-topics.sh --create --if-not-exists --topic bank-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

**Stop Kafka**

```bash
bin/kafka-server-stop.sh
```

> Do not reformat an existing KRaft data directory when restarting Kafka: that can destroy access to existing cluster data. On later runs, only use `kafka-server-start.sh`.

### Quick startup checklist

1. Start ZooKeeper and Kafka **or** start KRaft Kafka (not both).
2. Verify that the broker is available at `localhost:9092`.
3. Create `bank-topic` if it does not already exist.
4. Start the Spring Boot producer and consumer applications.
5. Send a bank transaction and check consumer output and group lag.
6. Stop your Spring Boot apps, then Kafka (and ZooKeeper if used).

### Windows note

For Windows command scripts, replace `bin/<name>.sh` with `bin\windows\<name>.bat`, for example:

```bat
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
bin\windows\kafka-server-start.bat config\server.properties
bin\windows\kafka-server-stop.bat
bin\windows\zookeeper-server-stop.bat
```

For KRaft on Windows, similarly use `bin\windows\kafka-storage.bat`, `bin\windows\kafka-server-start.bat`, and the `config\kraft\server.properties` path. Run the services in separate terminals.
