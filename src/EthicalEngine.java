import ethicalengine.*;
import ethicalengine.Character;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class EthicalEngine {
    private ArrayList<Scenario> scenarios = new ArrayList<>();
    // The following variables are used for readConfigFile. Note EthicalEngine does not have a constructor.
    private Scenario scenario;
    private boolean isLegalCrossing = false;
    private ArrayList<Character> passengers = new ArrayList<>();
    private ArrayList<Character> pedestrians = new ArrayList<>();
    private boolean firstScenario = true;
    private boolean validArguments = false;
    private Scanner scanner = new Scanner(System.in);
    private boolean saveUserResult;
    private boolean userContinue;

    public enum Decision{PASSENGERS, PEDESTRIANS}

    private static int processSaveScore(Character[] characters){
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

    public static Decision decide(Scenario scenario){
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
        switch (gender) {
            case "male":
                return Character.Gender.MALE;
            case "female":
                return Character.Gender.FEMALE;
            case "unknown":
                return Character.Gender.UNKNOWN;
            default:
                System.out.println("gender: " + gender);
                throw new InvalidCharacteristicException();
        }
    }

    private Character.BodyType getBodyType(String bodyType) throws InvalidCharacteristicException {
        switch (bodyType) {
            case "average":
                return Character.BodyType.AVERAGE;
            case "athletic":
                return Character.BodyType.ATHLETIC;
            case "overweight":
                return Character.BodyType.OVERWEIGHT;
            default:
                System.out.println("bodyType: " + bodyType);
                throw new InvalidCharacteristicException();
        }
    }

    private Person.Profession getProfession(String profession) throws InvalidCharacteristicException {
        switch (profession) {
            case "doctor":
                return Person.Profession.DOCTOR;
            case "ceo":
                return Person.Profession.CEO;
            case "criminal":
                return Person.Profession.CRIMINAL;
            case "homeless":
                return Person.Profession.HOMELESS;
            case "unemployed":
                return Person.Profession.UNEMPLOYED;
            case "unknown":
                return Person.Profession.UNKNOWN;
            case "none":
            case "":
                return Person.Profession.NONE;
            default:
                System.out.println("profession: " + profession);
                throw new InvalidCharacteristicException();
        }
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
            // TODO: Check if a path necessarily contains a "/"
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
        this.validArguments = true;
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
        this.validArguments = true;
//        String path = args[1];
    }

    private String readFile(Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("welcome.ascii"));
        return new String(encoded, encoding);
    }

    private void printInteractiveWelcome() {
        Charset cs = StandardCharsets.US_ASCII;
        try {
            String welcomeString = this.readFile(cs);
            System.out.println(welcomeString);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private boolean analyzeSavingInput(String input) throws InvalidInputException {
        if (input.equals("yes")) {
            return true;
        } else if (input.equals("no")) {
            return false;
        } else {
            throw new InvalidInputException("Invalid response. Do you consent to have your decisions saved to a file? (yes/no)");
        }
    }

    private void handleSavingDecision() {
        String input;
        System.out.println("Do you consent to have your decisions saved to a file? (yes/no)");
        do {
            input = this.scanner.nextLine();
            try { this.saveUserResult = this.analyzeSavingInput(input); }
            catch (InvalidInputException e) { System.out.println(e.getMessage()); }
        } while (!(input.equals("yes") || input.equals("no")));
    }

    private boolean analyzeContinueInput(String input) throws InvalidInputException {
        if (input.equals("yes")) {
            return true;
        } else if (input.equals("no")) {
            return false;
        } else {
            throw new InvalidInputException("Invalid response. Would you like to continue? (yes/no)");
        }
    }

    private void handleContinueDecision() {
        String input;
        System.out.println("Would you like to continue? (yes/no)");
        do {
            input = this.scanner.nextLine();
            try { this.userContinue = this.analyzeContinueInput(input); }
            catch (InvalidInputException e) { System.out.println(e.getMessage()); }
        } while (!(input.equals("yes") || input.equals("no")));
    }

    private Audit getUserAudit(boolean withConfig, Scenario... scenarios) {
        Audit audit = withConfig? new Audit(scenarios) : new Audit();
        audit.setAuditType("User");
        this.printInteractiveWelcome();
        this.handleSavingDecision();
        audit.saveUserDecision = this.saveUserResult;
        audit.scanner = this.scanner;
        return audit;
    }

    private void handleInteractive(String[] args) {
        if (args.length == 1) {
            this.validArguments = true;
            Audit audit = this.getUserAudit(false);
            int numScenarios = new Random().nextInt(10);
            audit.run(3);
            this.handleContinueDecision();
            while (this.userContinue) {
                audit.run(3);
                this.handleContinueDecision();
            }
        } else if (args.length == 3 && (args[1].equals("--config") || args[1].equals("-c"))) {
            this.validArguments = true;
            try {
                Scenario[] scenarios = this.readConfigFile(args[2]);
                Audit audit = this.getUserAudit(true, scenarios);
                audit.run();
                System.out.println("That's all. Press Enter to quit.");
                if(scanner.nextLine().isEmpty()) System.exit(-1);
            } catch (IOException e) {
                System.out.println("ERROR: could not find config file.");
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) {
//        for (String arg : args) System.out.println(arg);
        EthicalEngine ethicalEngine = new EthicalEngine();
        if (args.length > 0){
            String option = args[0];
            switch (option) {
                case "--config":
                case "-c":
                    ethicalEngine.handleControl(args);
                case "--help":
                case "-h":
                    ethicalEngine.handleHelp();
                case "--results":
                case "-r":
                    ethicalEngine.handleResults(args);
                case "--interactive":
                case "-i":
                    ethicalEngine.handleInteractive(args);
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
