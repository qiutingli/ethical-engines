import ethicalengine.Animal;
import ethicalengine.Character;
import ethicalengine.Person;
import ethicalengine.Scenario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

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

    private Scenario extractScenario(ArrayList<Character> passengers, ArrayList<Character> pedestrians, boolean isLegalCrossing){
        if (passengers.size() > 0 && pedestrians.size() > 0){
            Character[] pass = passengers.toArray(new Character[passengers.size()]);
            Character[] pedes = pedestrians.toArray(new Character[pedestrians.size()]);
            return new Scenario(pass, pedes, isLegalCrossing);
        }
        return null;
    }

    private Character generateCharacter(String[] characterAttributes){
        Character character;
        if (characterAttributes[0].equals("person")){
            character = new Person();
            switch (characterAttributes[1]) {
                case "male":
                    character.setGender(Character.Gender.MALE);
                    break;
                case "female":
                    character.setGender(Character.Gender.FEMALE);
                    break;
                default:
                    // TODO: Invalid input
            }
            // TODO: Handle invalid age
            character.setAge(Integer.parseInt(characterAttributes[2]));
            switch (characterAttributes[3]){
                case "average":
                    character.setBodyType(Character.BodyType.AVERAGE);
                    break;
                case "athletic":
                    character.setBodyType(Character.BodyType.ATHLETIC);
                    break;
                case "overweight":
                    character.setBodyType(Character.BodyType.OVERWEIGHT);
                    break;
                default:
                    // TODO: Invalid input
            }
        } else if (characterAttributes[0].equals("animal")) {
            character = new Animal(characterAttributes[7]);
            boolean isPet = Boolean.parseBoolean(characterAttributes[8]); // false if it is null
            ((Animal) character).setPet(isPet);
        } else {
            // TODO: Invalid input
            return null;
        }
        return character;
    }

    protected void readConfigFile(String pathToCsv) throws IOException {
        File csvFile = new File(pathToCsv);
        if (csvFile.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String csvLine;
                Scenario scenario;
                boolean isLegalCrossing = false;
                ArrayList<Character> passengers = new ArrayList<>();
                ArrayList<Character> pedestrians = new ArrayList<>();
                while ((csvLine = br.readLine()) != null) {
                    String[] characterAttributes = csvLine.split(",");
                    if (characterAttributes.length == 1){
                        if (characterAttributes[0].equals("scenario:green")){
                            isLegalCrossing = true;
                            scenario = extractScenario(passengers, pedestrians, isLegalCrossing);
                            if (scenario != null){
                                // TODO: Save scenario
                                System.out.println(scenario.toString());
                            }
                            passengers = new ArrayList<>();
                            pedestrians = new ArrayList<>();
                        } else if (characterAttributes[0].equals("scenario:red")){
                            isLegalCrossing = false;
                            scenario = extractScenario(passengers, pedestrians, isLegalCrossing);
                            if (scenario != null){
                                // TODO: Save scenario
                                System.out.println(scenario.toString());
                            }
                            passengers = new ArrayList<>();
                            pedestrians = new ArrayList<>();
                        } else {
                            // TODO: handle invalid config line
                        }
                    } else if (characterAttributes.length == 10){
                        Character character = generateCharacter(characterAttributes);
                        if (characterAttributes[9].equals("passenger")) passengers.add(character);
                        else pedestrians.add(character);
                    } else {
                        // TODO: handle invalid config line
                    }
                }
                if (passengers.size() > 0 && pedestrians.size() > 0){
                    // TODO: Handle isLegalCrossing not initiated
                    scenario = extractScenario(passengers, pedestrians, isLegalCrossing);
                    if (scenario != null){
                        // TODO: Save scenario
                        System.out.println(scenario.toString());
                    }
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
                    } catch (IOException e) {
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
