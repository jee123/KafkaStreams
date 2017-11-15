package com.kafka.streams.tushar;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by tjee on 11/14/17.
 */
public class StreamStarterApp {
    private static final Logger log = LoggerFactory.getLogger(StreamStarterApp.class);

    public static void main(String[] args) {

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-start-example");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KStreamBuilder builder = new KStreamBuilder();
        // 1 - stream data from kafka
        KStream<String, String> wordCountInput = builder.stream("word-count-input");
        // 2 - map values to lowercase
        KTable<String, Long> wordCounts = wordCountInput.mapValues(value -> value.toLowerCase())
                // 3 - flatmap values split by space
                .flatMapValues(value -> Arrays.asList(value.split(" ")))
                // 4 - selecting keys and applying them
                .selectKey((key, value) -> value)
                // 5 - group by key before we aggreagate
                .groupByKey()
                // 6 - count occurence
                .count("FinalCounts");

        wordCounts.to(Serdes.String(), Serdes.Long(), "word-count-output");
        // putting it all together
        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();
        //display topology
        log.info("TOPOLOGY looks like -->> " + streams.toString());
        //adding shutdown hook
        Runtime.getRuntime().addShutdownHook(
                new Thread(streams :: close));
    }


}
