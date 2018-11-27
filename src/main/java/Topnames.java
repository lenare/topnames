
import java.util.List;
import java.util.Properties;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Windowed;


public class Topnames {


    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "topnames-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KStreamBuilder builder = new KStreamBuilder();
        // 1 - stream from Kafka

        KStream<String, String> names = builder.stream("testnames");
        //KStream<Windowed<String>, List<String>> namesWindowed =

        names.print("KStream 'testnames'");

        KTable<String, Long> nameCounts = names
                // group by key before aggregation
                .groupByKey()
                // count occurrences
                .count("Counts");

        nameCounts.print();

        // to in order to write the results back to kafka
        nameCounts.to(Serdes.String(), Serdes.Long(), "countednames");


        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();


        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        // Update:
        // print the topology every 10 seconds for learning purposes
        /*while(true){
            System.out.println(streams.toString());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }*/



    }



}
