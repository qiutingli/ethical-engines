import ethicalengine.Animal;
import ethicalengine.Character;
import ethicalengine.Person;
import ethicalengine.Scenario;

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
                };
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

    public static void main(String[] args) {
        EthicalEngine ethicalEngine = new EthicalEngine();
        Character[] passengers = {new Animal("cat"), new Person()};
        Character[] pedestrians = {new Person(), new Person()};
        Scenario scenario = new Scenario(passengers, pedestrians, true);
        System.out.println(ethicalEngine.decide(scenario));
    }
}
