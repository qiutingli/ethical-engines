import ethicalengine.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Audit {
    private final ScenarioGenerator generator = new ScenarioGenerator();
    private final Random random = new Random();
    private final EthicalEngine engine = new EthicalEngine();
    private Scenario[] scenarios;
    private int runs;
    private String auditType = "Unspecified";
    private Map<String, ArrayList<Integer>> statsDict = new HashMap<>();
    private int totalSurval = 0;
    private int totalSurvivalAge = 0;
    public String resultPath = "logs/results.log";
    public EthicalEngine.Decision userDecision;
    public Scanner scanner;
    public boolean saveUserDecision;

    public Audit() {

    }

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

    /**
     * Update the statistics for one characteristic
     * @param characteristic - characteristic in stats
     * @param survived - indicates if the character is survived
     */
    private void updateOneKeyVal(String characteristic, boolean survived){
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

    /**
     * Update the all characteristic statistics related to the character
     * @param character - character
     * @param survived - indicates if the character is survived
     */
    private void updateCharacterRelatedStats(ethicalengine.Character character, boolean survived){
        if (character instanceof Person){
            String gender = character.getGender().toString().toLowerCase();
            String bodyType = character.getBodyType().toString().toLowerCase();
            String ageCategory = ((Person) character).getAgeCategory().toString().toLowerCase();
            String profession = ((Person) character).getProfession().toString().toLowerCase();
            String classType = "person";
            String[] characteristics = {ageCategory, gender, bodyType, classType};
            for (String characteristic : characteristics){
                this.updateOneKeyVal(characteristic, survived);
            }
            if (((Person) character).getAgeCategory() == Person.AgeCategory.ADULT) {
                this.updateOneKeyVal(profession, survived);
            }
            if (((Person) character).isPregnant()) { this.updateOneKeyVal("pregnant", survived); }
            if (((Person) character).isYou) { this.updateOneKeyVal("you", survived); }
        } else {
            this.updateOneKeyVal("animal", survived);
            String species = ((Animal) character).getSpecies();
            this.updateOneKeyVal(species, survived);
            if (((Animal) character).isPet()){
                this.updateOneKeyVal("pet", survived);
            }
        }
    }

    /**
     * Update survival age statistics
     * @param character - character
     * @param survived - indicates if the character is survived
     */
    private void updateSurvivalAgeStats(ethicalengine.Character character, boolean survived) {
        if (survived && character instanceof Person) {
            this.totalSurval += 1;
            this.totalSurvivalAge += character.getAge();
        }
    }

    /**
     * Update the statistics with the given scenario and the decision
     * @param scenario - scenario
     * @param decision - decision on saving whic group of characters
     */
    private void updateStats(Scenario scenario, EthicalEngine.Decision decision) {
        // Handle passengers
        for (ethicalengine.Character character : scenario.getPassengers()){
            boolean survived = decision == EthicalEngine.Decision.PASSENGERS;
            this.updateCharacterRelatedStats(character, survived);
            this.updateSurvivalAgeStats(character, survived);
            // Handle traffic lights
            this.updateOneKeyVal(scenario.isLegalCrossing()? "green" : "red", survived);
        }
        // Handle Pedestrians
        for (ethicalengine.Character character : scenario.getPedestrians()){
            boolean survived = decision == EthicalEngine.Decision.PEDESTRIANS;
            this.updateCharacterRelatedStats(character, survived);
            this.updateSurvivalAgeStats(character, survived);
            // Handle traffic lights
            this.updateOneKeyVal(scenario.isLegalCrossing()? "green" : "red", survived);
        }

    }

    /**
     * Add survival rates for the characteristic
     * @param characteristic - characteristic
     * @param rate - survival rate
     * @return String
     */
    private String addCharactSurvRate(String characteristic, double rate){
//        rate = Math.round(rate * 10) / 10.0;
        String truncatedRate = String.valueOf(rate).substring(0,3);
        return characteristic + ": " + truncatedRate + "\n";
    }

    /**
     * Produce the stats in survival rate descending order
     * @return TreeSet
     */
    public TreeSet<Map.Entry<String, Double>> generateDescendOrderedStats(){
        HashMap<String, Double> survivalStats = new HashMap<>();
        for (Map.Entry<String, ArrayList<Integer>> entry : this.statsDict.entrySet()){
            String characteristic = entry.getKey();
            ArrayList<Integer> survivalAndTotal = entry.getValue();
            double survivalRate =
                    Double.parseDouble(String.valueOf((double) survivalAndTotal.get(0)/survivalAndTotal.get(1)).substring(0,3));
            survivalStats.put(characteristic, survivalRate);
        }
        TreeSet<Map.Entry<String, Double>> sortedStats = new TreeSet(new Comparator<Map.Entry<String, Double>>(){
            @Override
            public int compare(Map.Entry<String, Double> me1, Map.Entry<String, Double> me2) {
                int result = (me2.getValue()).compareTo( me1.getValue() );
                if (result != 0) {
                    return result;
                } else {
                    return me1.getKey().compareTo(me2.getKey());
                }
            }
        });
        sortedStats.addAll(survivalStats.entrySet());

//        // Apply alphabet order and then descending order
//        Map<String, Double> sortedStats = new TreeMap<>(survivalStats);
//        sortedStats = sortedStats
//                .entrySet()
//                .stream()
//                .sorted(Collections.reverseOrder(Map.Entry.<String, Double>comparingByValue()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
//                        LinkedHashMap::new));
//        //      .forEach(System.out::println)
        return sortedStats;
    }

    /**
     * Generate the statistics
     * @return String
     */
    public String generateStats(){
        StringBuilder summaryStringBuilder = new StringBuilder(
                "======================================\n" +
                "# " + this.auditType + " Audit\n" +
                "======================================\n" +
                "- % SAVED AFTER ").append(this.runs).append(" RUNS\n");
        TreeSet<Map.Entry<String, Double>> descendOrderedStats = generateDescendOrderedStats();
        for (Map.Entry<String, Double> entry : descendOrderedStats) {
            String characteristic = entry.getKey();
            double survivalAndTotal = entry.getValue();
            summaryStringBuilder.append(addCharactSurvRate(characteristic, survivalAndTotal));
        }
        String averageAge = ((double) this.totalSurvivalAge / this.totalSurval + "");
        averageAge = averageAge.substring(0, averageAge.indexOf(".")+2);
        summaryStringBuilder.append("--\n" + "average age: ").append(averageAge).append("\n");
        return summaryStringBuilder.toString();
    }

    @Override
    public String toString() {
        return generateStats();
    }

    public void printStatistics(){
        /*
        Print Statistics
         */
        System.out.print(this.toString());
    }

    /**
     * print the audit result to file
     * @param filepath - result file pat
     */
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

    /**
     * Handle User Decision Input
     */
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

    /**
     * Run the audit
     * @param runs - number of scenarios
     */
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
            this.printStatistics();
            if (this.saveUserDecision) { this.printToFile("logs/user.log"); }
        } else {
            for (int i = 0; i < runs; i++) {
                Scenario scenario = this.generator.generate();
                EthicalEngine.Decision decision = EthicalEngine.decide(scenario);
                updateStats(scenario, decision);
            }
            this.printStatistics();
            this.printToFile("logs/results.log");
        }
    }

    /**
     * Run audit with random scenarios
     */
    public void run(){
        int runsAcc = 0;
        if (this.auditType.equals("User")) {
            for (Scenario value : this.scenarios) {
                if (runsAcc == 3) {
                    runsAcc = 0;
                    this.printStatistics();
                    System.out.println("Would you like to continue? (yes/no)");
                    String input = this.scanner.nextLine();
                    if (input.equals("no")) break;
                }
                runsAcc++;
                this.runs ++;
                Scenario scenario = value;
                System.out.println(scenario);
                System.out.println("Who should be saved? (passenger(s) [1] or pedestrian(s) [2])");
                this.handleUserDecisionInput();
                updateStats(scenario, this.userDecision);
            }
            this.printStatistics();
            if (this.saveUserDecision) { this.printToFile("logs/user.log"); }
        } else {
            for (Scenario scenario : this.scenarios) {
                EthicalEngine.Decision decision = EthicalEngine.decide(scenario);
                updateStats(scenario, decision);
            }
            this.printToFile(this.resultPath);
        }
    }


//    public static void main(String[] args) throws IOException {
//        Audit audit = new Audit();
//        audit.run(10);
//        audit.run(50);
//        audit.run(100);
//
//        EthicalEngine engine = new EthicalEngine();
//        Scenario[] scenarios = engine.readConfigFile("SelfTest/data/config1");
//        Audit audit1 = new Audit(scenarios);
//        audit1.run();
//        System.out.println("logs/results.log".substring("logs/results.log".indexOf("/")+1));
//    }
}

