package ethicalengine;


public class Scenario {
    private final Character[] passengers;
    private final Character[] pedestrians;
    private boolean isLegalCrossing;

    public Scenario(Character[] passengers, Character[] pedestrians, boolean isLegalCrossing){
        this.passengers = passengers;
        this.pedestrians = pedestrians;
        this.isLegalCrossing = isLegalCrossing;
    }

    public boolean hasYouInCar(){
        for (Character character : passengers) {
            if (character instanceof  Person && ((Person) character).isYou){
                return true;
            }
        }
        return false;
    }

    public boolean hasYouInLane(){
        for (Character character : pedestrians) {
            if (character instanceof  Person && ((Person) character).isYou){
                return true;
            }
        }
        return false;
    }

    public Character[] getPassengers(){
        return this.passengers;
    }

    public Character[] getPedestrians(){
        return this.pedestrians;
    }

    public boolean isLegalCrossing(){
        return this.isLegalCrossing;
    }

    public void setLegalCrossing(boolean isLegalCrossing){
        this.isLegalCrossing = isLegalCrossing;
    }

    public int getPassengerCount(){
        return this.passengers.length;
    }

    public int getPedestrianCount(){
        return this.pedestrians.length;
    }

    @Override
    public String toString() {
        StringBuilder summary = new StringBuilder(
                "======================================\n" +
                "# Scenario\n" +
                "======================================\n" +
                "Legal Crossing: " + (this.isLegalCrossing? "yes" : "no") + "\n" +
                "Passengers (" + this.getPassengerCount() + ")");
        for (Character character : this.passengers){
            summary.append("\n").append("- ").append(character.toString());
        }
        summary.append("\n").append("Pedestrians (").append(this.getPedestrianCount()).append(")");
        for (Character character : this.pedestrians){
            summary.append("\n").append("- ").append(character.toString());
        }
        return summary.toString();
    }

    // For test
    public static void main(String[] args) {
        Character[] passengers = {new Person(20, Person.Profession.HOMELESS, Character.Gender.FEMALE,
                Character.BodyType.ATHLETIC, false)};
        Character[] pedestrians = {
                new Person(120, Character.Gender.MALE,
                Character.BodyType.ATHLETIC)
//                new Person(20, Person.Profession.DOCTOR, Character.Gender.FEMALE,
//                Character.BodyType.AVERAGE, true)
        };
        Scenario scenario = new Scenario(passengers, pedestrians, true);
        System.out.println(scenario.toString());
    }
}
