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
                "Passengers (" + this.getPassengerCount() + ")\n");
        for (Character character : this.passengers){
            summary.append("- ").append(character.toString()).append("\n");
        }
        summary.append("Pedestrians (").append(this.getPedestrianCount()).append(")\n");
        for (Character character : this.pedestrians){
            summary.append("- ").append(character.toString()).append("\n");
        }
        return summary.toString();
    }

    // For test
    public static void main(String[] args) {
        Character[] passengers = {new Animal("cat"), new Person(5, Person.Profession.NONE, Character.Gender.MALE,
                Character.BodyType.OVERWEIGHT, false)};
        Character[] pedestrians = {new Person(3, Person.Profession.NONE, Character.Gender.MALE,
                Character.BodyType.AVERAGE, false),
                new Person(20, Person.Profession.DOCTOR, Character.Gender.FEMALE,
                Character.BodyType.AVERAGE, true)};
        Scenario scenario = new Scenario(passengers, pedestrians, true);
        System.out.println(scenario.toString());
    }
}
