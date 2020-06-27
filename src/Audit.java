import ethicalengine.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Audit {
    private final ScenarioGenerator generator = new ScenarioGenerator();
    private final Random random = new Random();
    private final EthicalEngine engine = new EthicalEngine();
    private Scenario[] scenarios;
    private int runs;
    private String auditType = "Unspecified";
    private Map<String, ArrayList<Integer>> statsDict = new HashMap<>();
    private int totalPeople = 0;
    private int totalAge = 0;
    public String resultPath = "logs/results.log";
    public EthicalEngine.Decision userDecision;
    public Scanner scanner;
    public boolean saveUserDecision;

    public Audit() { }

    public Audit(Scenario[] scenarios) {
        this.scenarios = scenarios;
    }

    public void setAuditType(String name){
        this.auditType = name;
    }

    public String getAuditType(){
        return this.auditType;
    }

    public void setCharacterNumbers(){
        int passMax = this.random.nextInt(5) + 1; // To avoid the number equals to zero
        int passMin = this.random.nextInt(passMax) + 1;
        int pedesMax = this.random.nextInt(5) + 1;
        int pedesMin = this.random.nextInt(pedesMax) + 1;
        this.generator.setPassengerCountMax(passMax);
        this.generator.setPassengerCountMin(passMin);
        this.generator.setPedestrianCountMax(pedesMax);
        this.generator.setPedestrianCountMin(pedesMin);
    }

    private void updateOneKeyVal(String characteristic, boolean survived){
        /*
        Update the characteristic statistics
         */
        ArrayList<Integer> survivalAndTotal;
        int survivalIncrease = survived? 1:0;
        survivalAndTotal = new ArrayList<>(Arrays.asList(survivalIncrease, 1));
        try {
            // Update statistics if the key exists
            survivalAndTotal.set(0, this.statsDict.get(characteristic).get(0) + survivalAndTotal.get(0));
            survivalAndTotal.set(1, this.statsDict.get(characteristic).get(1) + survivalAndTotal.get(1));
            this.statsDict.put(characteristic, survivalAndTotal);
        } catch (Exception e){
            // Update statistics if the key does not exist
            this.statsDict.putIfAbsent(characteristic, survivalAndTotal);
        }
    }

    private void updateCharacterRelatedStats(ethicalengine.Character character, boolean survived){
        /*
        Update the all characteristic statistics related to the character
        • age category
        • gender
        • body type
        • profession
        • pregnant
        • class type (person or animal)
        • species
        • pets
        • legality (red or green light)
         */
        if (character instanceof Person){
            this.totalPeople += 1;
            this.totalAge += character.getAge();
            String ageCategory = ((Person) character).getAgeCategory().toString().toLowerCase();
            String gender = ((Person) character).getGender().toString().toLowerCase();
            String bodyType = ((Person) character).getBodyType().toString().toLowerCase();
            String profession = ((Person) character).getProfession().toString().toLowerCase();
            String classType = "person";
            String[] characteristics = {ageCategory, gender, bodyType, profession, classType};
            for (String characteristic : characteristics){
                this.updateOneKeyVal(characteristic, survived);
            }
            if (((Person) character).isPregnant()){ this.updateOneKeyVal("pregnant", survived); }
        } else {
            String species = ((Animal) character).getSpecies();
            this.updateOneKeyVal(species, survived);
            if (((Animal) character).isPet()){
                this.updateOneKeyVal("pet", survived);
            }
        }
    }

    private void updateStats(Scenario scenario, EthicalEngine.Decision decision){
        /*
        Update the statistics for the given scenario
         */
        // Handle passengers
        for (ethicalengine.Character character : scenario.getPassengers()){
            boolean survived = decision == EthicalEngine.Decision.PASSENGERS;
            this.updateCharacterRelatedStats(character, survived);
        }
        // Handle Pedestrians
        for (ethicalengine.Character character : scenario.getPedestrians()){
            boolean survived = decision == EthicalEngine.Decision.PEDESTRIANS;
            this.updateCharacterRelatedStats(character, survived);
        }
        // Handle traffic lights
        if (scenario.isLegalCrossing()){
            boolean survived = decision == EthicalEngine.Decision.PEDESTRIANS;
            this.updateOneKeyVal("green", survived);
        } else {
            boolean survived = decision == EthicalEngine.Decision.PASSENGERS;
            this.updateOneKeyVal("red", survived);
        }
    }

    private String AddCharactSurvRate(String characteristic, double rate){
        rate = Math.round(rate * 10) / 10.0;
        return characteristic + ": " + rate + "\n";
    }

    public HashMap<String, Double> generateDescendOrderedStats(){
        /*
        Produce stats in descending order
         */
        HashMap<String, Double> sortedStats = new HashMap<>();
        for (Map.Entry<String, ArrayList<Integer>> entry : this.statsDict.entrySet()){
            String characteristic = entry.getKey();
            ArrayList<Integer> survivalAndTotal = entry.getValue();
            sortedStats.put(characteristic, (double) survivalAndTotal.get(0)/survivalAndTotal.get(1));
        }
        sortedStats = sortedStats
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.<String, Double>comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
        //      .forEach(System.out::println)
        System.out.println();
        return sortedStats;
    }

    public String generateStatistic(){
        StringBuilder summaryStringBuilder = new StringBuilder("======================================\n" +
                "# " + this.auditType + " Audit\n" +
                "======================================\n" +
                "- % SAVED AFTER ").append(this.runs).append(" RUNS\n");
        HashMap<String, Double> descendOrderedStats = generateDescendOrderedStats();
        for (Map.Entry<String, Double> entry : descendOrderedStats.entrySet()) {
            String characteristic = entry.getKey();
            double survivalAndTotal = entry.getValue();
            summaryStringBuilder.append(AddCharactSurvRate(characteristic, survivalAndTotal));
        }
        String averageAge = ((double) this.totalAge/this.totalPeople + "");
        averageAge = averageAge.substring(0, averageAge.indexOf(".")+2);
        summaryStringBuilder.append("--\n" + "average age: ").append(averageAge);
        return summaryStringBuilder.toString();
    }

    @Override
    public String toString() {
        return generateStatistic();
    }

    public void printStatistic(){
        System.out.println(this.toString());
    }

    public void printToFile(String filepath){
        String directoryName = filepath.substring(0, filepath.indexOf("/"));
        String fileName = filepath.substring(0, filepath.indexOf("/"));
        File directory = new File(directoryName);
        if (directoryName.equals("logs") && ! directory.exists()){
            // If require it to make the entire directory path including parents, use directory.mkdirs() instead.
            directory.mkdir();
        }
        File file = new File(filepath);
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(this.toString());
            bw.close();
        }
        catch (IOException e){
//            e.printStackTrace();
            System.out.println("ERROR: could not print results. Target directory does not exist.");
            System.exit(-1);
        }
    }

    private void handleUserDecisionInput(){
        String input = this.scanner.nextLine();
        switch (input) {
            case "passenger":
            case "passengers":
            case "1":
                this.userDecision = EthicalEngine.Decision.PASSENGERS;
                break;
            case "pedestrian":
            case "pedestrians":
            case "2":
                this.userDecision = EthicalEngine.Decision.PEDESTRIANS;
                break;
        }
    }

    public void run(int runs){
        this.runs += runs;
        if (this.auditType.equals("User")) {
            for (int i = 0; i < runs; i++) {
                Scenario scenario = this.generator.generate();
                System.out.println(scenario);
                System.out.println("Who should be saved? (passenger(s) [1] or pedestrian(s) [2])");
                handleUserDecisionInput();
                updateStats(scenario, this.userDecision);
            }
            this.printStatistic();
            if (this.saveUserDecision) { this.printToFile("logs/user.log"); }
        } else {
            for (int i = 0; i < runs; i++) {
                Scenario scenario = this.generator.generate();
                EthicalEngine.Decision decision = EthicalEngine.decide(scenario);
                updateStats(scenario, decision);
            }
            this.printStatistic();
            this.printToFile("logs/results.log");
        }
    }

    public void run(){
        int runsAcc = 0;
        if (this.auditType.equals("User")) {
            for (Scenario value : this.scenarios) {
                if (runsAcc == 3) {
                    this.runs += runsAcc;
                    this.printStatistic();
                    System.out.println("Would you like to continue? (yes/no)");
                    String input = this.scanner.nextLine();
                    if (input.equals("no")) break;
                    runsAcc = 0;
                }
                runsAcc++;
                Scenario scenario = value;
                System.out.println(scenario);
                System.out.println("Who should be saved? (passenger(s) [1] or pedestrian(s) [2])");
                this.handleUserDecisionInput();
                updateStats(scenario, this.userDecision);
            }
            this.printStatistic();
            if (this.saveUserDecision) { this.printToFile("logs/user.log"); }
        } else {
            for (Scenario scenario : this.scenarios) {
                EthicalEngine.Decision decision = EthicalEngine.decide(scenario);
                updateStats(scenario, decision);
            }
            this.printToFile(this.resultPath);
        }
    }

    public static void main(String[] args) throws IOException {
        Audit audit = new Audit();
        audit.run(10);
        audit.run(50);
        audit.run(100);

//        EthicalEngine engine = new EthicalEngine();
//        Scenario[] scenarios = engine.readConfigFile("SelfTest/data/config1");
//        Audit audit1 = new Audit(scenarios);
//        audit1.run();
//        System.out.println("logs/results.log".substring("logs/results.log".indexOf("/")+1));
    }
}

