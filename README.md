# Aggregation-server 3-step manual

## Step 1: Compile all files
```shell
javac AggregationServer.java ContentServer.java GETClient.java 
```

## Step 2: Run Aggregation Server
```shell
java AggregationServer
```

## Step 3: Run other client in any order
```shell
java GETClient localhost:4567
```

```shell
java ContentServer localhost:4567 weather_data.txt
```

# What is missing in this draft version
- Testing
- Additional params for AggregationServer, Content Server and GET Client run command
- Lamport Clock value is not fully implement in the AggregationServer as no implementation to handle incoming value and the server value
- 