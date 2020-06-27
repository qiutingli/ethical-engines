import ethicalengine.*;
import ethicalengine.Character;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class EthicalEngine {
    private ArrayList<Scenario> scenarios = new ArrayList<>();
    // The following variables are used for readConfigFile. Note EthicalEngine does not have a constructor.
    private Scenario scenario;
    private boolean isLegalCrossing = false;
    private ArrayList<Character> passengers = new ArrayList<>();
    private ArrayList<Character> pedestrians = new ArrayList<>();
    private boolean firstScenario = true;
    private boolean validArguments = false;

    public enum Decision{PASSENGERS, PEDESTRIANS}

    private int processSaveScore(Character[] characters){
        int saveScoreAcc = 0;
        for (Character character : characters){
            if (character instanceof Person){
                saveScoreAcc += 1;
                if (((Person) character).isPregnant()) saveScoreAcc += 1;
                if (character.getBodyType() == Character.BodyType.ATHLETIC) saveScoreAcc -= 0.5;
                if (((Person) character).getProfession() == Person.Profession.DOCTOR) saveScoreAcc += 0.5;
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

    private Character.Gender getGender(String gender) throws InvalidCharacteristicException {
        return switch (gender) {
            case "male" -> Character.Gender.MALE;
            case "female" -> Character.Gender.FEMALE;
            case "unknown" -> Character.Gender.UNKNOWN;
            default -> throw new InvalidCharacteristicException();
        };
    }

    private Character.BodyType getBodyType(String bodyType) throws InvalidCharacteristicException {
        return switch (bodyType) {
            case "average" -> Character.BodyType.AVERAGE;
            case "athletic" -> Character.BodyType.ATHLETIC;
            case "overweight" -> Character.BodyType.OVERWEIGHT;
            default -> throw new InvalidCharacteristicException();
        };
    }

    private Person.Profession getProfession(String profession) throws InvalidCharacteristicException {
        return switch (profession) {
            case "doctor" -> Person.Profession.DOCTOR;
            case "ceo" -> Person.Profession.CEO;
            case "criminal" -> Person.Profession.CRIMINAL;
            case "homeless" -> Person.Profession.HOMELESS;
            case "unemployed" -> Person.Profession.UNEMPLOYED;
            case "unknown" -> Person.Profession.UNKNOWN;
            case "" -> Person.Profession.NONE;
            default -> throw new InvalidCharacteristicException();
        };
    }

    private Character generateCharacter(String[] characterAttributes) throws NumberFormatException, InvalidCharacteristicException {
        Character character;
        if (characterAttributes[0].equals("person")){
            character = new Person();
            character.setGender(this.getGender(characterAttributes[1]));
            try { character.setAge(Integer.parseInt(characterAttributes[2])); }
            catch (Exception e) { throw new NumberFormatException(); }
            character.setBodyType(this.getBodyType(characterAttributes[3]));
            ((Person) character).setProfession(this.getProfession(characterAttributes[4]));
            ((Person) character).setPregnant(Boolean.parseBoolean(characterAttributes[5]));
            ((Person) character).setAsYou(Boolean.parseBoolean(characterAttributes[6]));
        } else if (characterAttributes[0].equals("animal")) {
            character = new Animal(characterAttributes[7]);
            boolean isPet = Boolean.parseBoolean(characterAttributes[8]); // false if it is null
            ((Animal) character).setPet(isPet);
        } else {
            System.out.println(String.join(",", characterAttributes));
            throw new InvalidCharacteristicException();
        }
        return character;
    }

    private void saveScenario(Scenario scenario){
        if (scenario != null){
            this.scenarios.add(scenario);
        }
    }

    private void handleScenarioBreaks(String[] characterAttributes) throws InvalidDataFormatException {
        String scenarioStart = characterAttributes[0];
        if (scenarioStart.equals("scenario:green") || scenarioStart.equals("scenario:red")){
            if (this.firstScenario) {
                this.isLegalCrossing = scenarioStart.equals("scenario:green");
                this.firstScenario = false;
            } else {
                this.scenario = extractScenario(this.passengers, this.pedestrians, this.isLegalCrossing);
                this.saveScenario(scenario);
                this.isLegalCrossing = scenarioStart.equals("scenario:green");
                this.passengers = new ArrayList<>();
                this.pedestrians = new ArrayList<>();
            }
        } else {
            System.out.println("Invalid traffic light line: " + String.join(",", characterAttributes));
            throw new InvalidDataFormatException();
        }
    }

    private void handleScenarioCharacters(String[] characterAttributes) throws InvalidCharacteristicException {
        if (!String.join(",", characterAttributes)
                .equals("class,gender,age,bodyType,profession,pregnant,isYou,species,isPet,role")){
            Character character = generateCharacter(characterAttributes);
            if (characterAttributes[9].equals("passenger")) this.passengers.add(character);
            else if (characterAttributes[9].equals("pedestrian")) this.pedestrians.add(character);
            else throw new InvalidCharacteristicException();
        }
    }

    private void handleExceptions(Exception e, int lineCount){
        if (e instanceof InvalidDataFormatException)
            System.out.println("WARNING: invalid data format in config file in line " + lineCount);
        else if (e instanceof NumberFormatException)
            System.out.println("WARNING: invalid number format in config file in line " + lineCount);
        else if (e instanceof InvalidCharacteristicException)
            System.out.println("WARNING: invalid characteristic in config file in line " + lineCount);
        else e.printStackTrace();
    }

    protected Scenario[] readConfigFile(String pathToCsv) throws IOException {
        File csvFile = new File(pathToCsv);
        if (csvFile.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String csvLine;
                int lineCount = 1;
                while ((csvLine = br.readLine()) != null) {
                    try {
                        String[] characterAttributes = csvLine.split(",");
                        if (characterAttributes.length == 1) handleScenarioBreaks(characterAttributes);
                        else if (characterAttributes.length == 10) handleScenarioCharacters(characterAttributes);
                        else throw new InvalidDataFormatException();
                    } catch (Exception e) {
                        handleExceptions(e, lineCount);
                    }
                    lineCount++;
                }
                if (this.passengers.size() > 0 && this.pedestrians.size() > 0){
                    // Handle the last scenario
                    this.scenario = extractScenario(this.passengers, this.pedestrians, this.isLegalCrossing);
                    this.saveScenario(this.scenario);
                }
                return this.scenarios.toArray(new Scenario[this.scenarios.size()]);
            }
        } else throw new IOException();
    }

    private void handleControl(String[] args) {
        if (args.length == 1) {
            this.handleHelp();
        } else if (args[1].contains("/")){
            if (args[1].substring(0, args[1].lastIndexOf("/")).equals("SelfTest/data")){
                this.validArguments = true;
                try {
                    Scenario[] scenarios = this.readConfigFile(args[1]);
                    for (Scenario scenario : scenarios){ System.out.println(scenario.toString()); }
                } catch (IOException e) {
                    System.out.println("ERROR: could not find config file.");
                    System.exit(-1);
                }
            }
        }
    }

    private void handleHelp() {
        System.out.println(
                "EthicalEngine - COMP90041 - Final Project\n\n" +
                        "Usage: java EthicalEngine [arguments]\n\n" +
                        "Arguments:\n" +
                        "-c or --config      Optional: path to config file\n" +
                        "-h or --help        Print Help (this message) and exit\n" +
                        "-r or --results     Optional: path to results log file\n" +
                        "-i or --interactive Optional: launches interactive mode");
    }

    private void handleResults(String[] args) {
        String path = args[1];
    }

    private void handleInteractive(String[] args) {
        if (args.length == 1) {
            ScenarioGenerator generator = new ScenarioGenerator();
        }
    }

    public static void main(String[] args) {
        EthicalEngine ethicalEngine = new EthicalEngine();
        Audit audit= new Audit();
        if (args.length > 0){
            String option = args[0];
            switch (option) {
                case "--config", "-c" -> ethicalEngine.handleControl(args);
                case "--help", "-h" -> ethicalEngine.handleHelp();
                case "--results", "-r" -> ethicalEngine.handleResults(args);
                case "--interactive", "-i" -> ethicalEngine.handleInteractive(args);
            }
        }
        if (!ethicalEngine.validArguments) System.out.println("ERROR: Invalid command arguments.");

//        // For Test
//        Character[] passengers = {new Animal("cat"), new Person()};
//        Character[] pedestrians = {new Person(), new Person()};
//        Scenario scenario = new Scenario(passengers, pedestrians, true);
//        System.out.println(ethicalEngine.decide(scenario));

//        Options options = new Options();
//        Option input = new Option("i", "input", true, "input file path");
//        input.setRequired(true);
//        options.addOption(input);

    }
}
