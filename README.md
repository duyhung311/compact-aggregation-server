# Aggregation-server 3-step manual

## Step 1: Compile all files
```shell
javac -d compile/ AggregationServer.java ContentServer.java GETClient.java 
```

## Step 2: Run Aggregation Server
```shell
java -cp compile AggregationServer
```

## Step 3: Run other client in any order
```shell
java -cp compile GETClient localhost:4567
```

```shell
java -cp compile ContentServer localhost:4567 weather_data.txt
```

# Testing Prerequisite
- Java 21 (also a prerequisite to run the program)
- JUnit 4.12
- Junit 5.8.1
- Mockito Core 5.12.0

# Testing
Each component has an associated test file that comprises both **integration** and **unit** test <br>
_*Test suites are included in the design file_ <br>

I used IntelliJ to run test so i dont know the command to run test files<br>
What i do to run test is to import the Mockito, Junit4 and Junit5 and restart IntelliJ to press run button
# What is added after th draft version
- Testing (Integration and unit test)
- Additional params for AggregationServer, Content Server and GET Client run command
- Lamport Clock value is fully implemented and tested in the AggregationServer.
- 