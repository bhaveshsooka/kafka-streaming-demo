package za.co.goldmine.kafka.streams.fitness_duplicate_detector.streams_builder;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import za.co.goldmine.kafka.streams.fitness_duplicate_detector.avro.Fitness;
import za.co.goldmine.kafka.streams.fitness_duplicate_detector.avro.FitnessHash;

import java.util.Collections;
import java.util.Map;

import static za.co.goldmine.kafka.streams.fitness_duplicate_detector.business_logic.FitnessTransformationLogic.generateHash;


public class FitnessFlowsBuilder {

    // Builder pattern

    private final StreamsBuilder builder = new StreamsBuilder();
    private final Map<String, String> schemaRegistryMap = Collections
      .singletonMap("schema.registry.url", "http://schema-registry:8081");

    public FitnessFlowsBuilder setupDuplicateDetectorFlow(String inTopic, String outTopic) {

        Serde<FitnessHash> fitnessHashSerdeAvroSerde = new SpecificAvroSerde<>();
        fitnessHashSerdeAvroSerde.configure(schemaRegistryMap, false);

        builder
          .stream(inTopic, Consumed.with(Serdes.String(), fitnessHashSerdeAvroSerde))
          .groupBy((key, value) -> value.getMYFRAUDKEY().toString())
          .count()
          .filter((key, value) -> value > 1)
          .toStream()
          .filter((key, value) -> value != null)
          .to(outTopic, Produced.with(Serdes.String(), Serdes.Long()));

        return this;
    }

    public FitnessFlowsBuilder setupHashGeneratorFlow(String inTopic, String outTopic) {

        Serde<Fitness> fitnessAvroSerde = new SpecificAvroSerde<>();
        fitnessAvroSerde.configure(schemaRegistryMap, false);

        Serde<FitnessHash> fitnessHashAvroSerde = new SpecificAvroSerde<>();
        fitnessHashAvroSerde.configure(schemaRegistryMap, false);

        builder
          .stream(inTopic, Consumed.with(Serdes.String(), fitnessAvroSerde))
          .map((String key, Fitness value) -> KeyValue.pair(key, generateHash(value)))
          .to(outTopic, Produced.with(Serdes.String(), fitnessHashAvroSerde));

        return this;
    }

    public FitnessFlowsBuilder setupDuplicateGeneratorFlow(String topic)
    {
        final int[] messageCount = {0};

        Serde<Fitness> fitnessAvroSerde = new SpecificAvroSerde<>();
        fitnessAvroSerde.configure(schemaRegistryMap, false);

        builder
          .stream(topic, Consumed.with(Serdes.String(), fitnessAvroSerde))
          .map((key, value) -> {
              messageCount[0]++;
              return KeyValue.pair(key, value);
          })
          .filter((key, value) -> messageCount[0] % 10 == 0)
          .map((key, value) -> KeyValue.pair("duplicate", value))
          .to(topic, Produced.with(Serdes.String(), fitnessAvroSerde));

        return this;
    }

    public StreamsBuilder getBuilder() {
        return this.builder;
    }
}
