import ethicalengine.Character;
import ethicalengine.Person;
import ethicalengine.Scenario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EthicalEngine {

    public enum Decision{PASSENGERS, PEDESTRIANS}

    private int processSaveScore(Character[] characters){
        int saveScoreAcc = 0;
        for (Character character : characters){
            if (character instanceof Person){
                saveScoreAcc += 1;
                if (((Person) character).isPregnant()){
                    saveScoreAcc += 1;
                }
                if (character.getBodyType() == Character.BodyType.ATHLETIC){
                    saveScoreAcc -= 0.5;
                }
                if (((Person) character).getProfession() == Person.Profession.DOCTOR){
                    saveScoreAcc += 0.5;
                }
            }
        }
        return saveScoreAcc;
    }

    public Decision decide(Scenario scenario){
        // Save passenger if saveScore >= 0, save pedestrians otherwise.
        int saveScore = scenario.isLegalCrossing()? -1 : 1;
        int acc1 = processSaveScore(scenario.getPassengers());
        int acc2 = processSaveScore(scenario.getPedestrians());
        saveScore += acc1 - acc2;
        return (saveScore >= 0? Decision.PASSENGERS : Decision.PEDESTRIANS);
    }

    protected void readConfigFile(String pathToCsv) throws IOException {
        File csvFile = new File(pathToCsv);
        if (csvFile.isFile()) {
//            String row = "";
//            BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
//            while ((row = csvReader.readLine()) != null) {
//                String[] data = row.split(",");
//                System.out.println(data);
//            }
//            csvReader.close();
            String line;
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                while ((line = br.readLine()) != null) {
                    String[] characteristics = line.split(",");
                    System.out.println("Characteristics [class= " + characteristics[0] + "]");
                }
            }
        } else {
            throw new IOException();
        }
    }

    public static void main(String[] args) {
        EthicalEngine ethicalEngine = new EthicalEngine();
        boolean validArguments = false;
        if (args.length > 1){
            if ((args[0].equals("--config") || args[0].equals("-c")) && args[1].contains("/")){
                if (args[1].substring(0, args[1].lastIndexOf("/")).equals("SelfTest/data")){
                    validArguments = true;
                    try {
                        ethicalEngine.readConfigFile(args[1]);
                    } catch (IOException e){
                        System.out.println("ERROR: could not find config file.");
                        System.exit(-1);
                    }
                }
            }
        }
        if (!validArguments) System.out.println("ERROR: Invalid command arguments.");
//        // For Test
//        Character[] passengers = {new Animal("cat"), new Person()};
//        Character[] pedestrians = {new Person(), new Person()};
//        Scenario scenario = new Scenario(passengers, pedestrians, true);
//        System.out.println(ethicalEngine.decide(scenario));
    }
}
