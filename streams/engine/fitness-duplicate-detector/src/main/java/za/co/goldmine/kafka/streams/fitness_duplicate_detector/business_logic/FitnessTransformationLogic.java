package za.co.goldmine.kafka.streams.fitness_duplicate_detector.business_logic;

import za.co.goldmine.kafka.streams.fitness_duplicate_detector.avro.Fitness;
import za.co.goldmine.kafka.streams.fitness_duplicate_detector.avro.FitnessHash;

public class FitnessTransformationLogic {

    public static FitnessHash generateHash(Fitness fitnessEvent) {
        FitnessHash fitnessHash = new FitnessHash();

        String hash = ""
          + fitnessEvent.getStartTimeInSeconds()
          + fitnessEvent.getDurationInSeconds()
          + fitnessEvent.getActiveKilocalories()
          + fitnessEvent.getSteps();


        fitnessHash.setUserid(fitnessEvent.getUserid());
        fitnessHash.setTenantId(fitnessEvent.getTenantId());
        fitnessHash.setMYFRAUDKEY(hash);

        return fitnessHash;
    }
}
