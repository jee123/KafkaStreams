package com.kafka.streams.tushar;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by tjee on 11/16/17.
 */
public class UserDataProducer {
    private static final Logger log = LoggerFactory.getLogger(UserDataProducer.class);
    public static void main(String[] args) throws Exception{
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer
                .class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer
                .class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        Producer<String, String> producer = new KafkaProducer<>(properties);


        //case 1 : creating new user
        log.info("CREATING NEW USER");
        producer.send(userRecord("john", "First=John,Last=Doe,Email=john@doe.com")).get();
        producer.send(purchaseRecord("john", "Marlboro(1)")).get();

        Thread.sleep(10000);

        //case 2 : receive user purchase but user does not exist in kafka
        log.info("USER NOT IN SYSTEM SO ADD!!");
        producer.send(purchaseRecord("mark", "instagram(2)")).get();
        Thread.sleep(10000);

        //case 3 : information update for john
        log.info("NEW TRANSACTION AND INFO UPDATE FOR EXISTING USER");
        producer.send(userRecord("john", "First=Johnathan,Last=Doe,Email=john@doe.com")).get();
        producer.send(purchaseRecord("john", "classic(3)"));

        Thread.sleep(10000);

        //case 4 : false alarm for steph
        log.info("NON EXISTING USER AND THEN USER");
        producer.send(purchaseRecord("steph", "uber(4)")).get();
        producer.send(userRecord("steph", "First=stephen,Last=watson,GitHub=jsteph")).get();
        producer.send(purchaseRecord("steph", "lyft(4)")).get();
        producer.send(userRecord("steph", null)).get();

        Thread.sleep(10000);

        // 5 - we create a user, but it gets deleted before any purchase comes through
        log.info("USER EXISTED BUT DELETE BEFORE PURCHASE CAME THROUGH!");
        producer.send(userRecord("alice", "First=Alice")).get();
        producer.send(userRecord("alice", null)).get(); // that's the delete record
        producer.send(purchaseRecord("alice", "Apache Kafka Series (5)")).get();

        Thread.sleep(10000);

        log.info("^^^^^^### finished cases ^^^^^### ");
        producer.close();
    }

    private static ProducerRecord<String, String> userRecord (String key, String
            value){
            return new ProducerRecord<>("user-table" ,key, value);
    }



    private static ProducerRecord<String, String> purchaseRecord (String key, String
            value){
        return new ProducerRecord<>("user-purchases" ,key, value);
    }



}
