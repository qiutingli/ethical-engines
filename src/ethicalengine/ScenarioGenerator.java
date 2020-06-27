package ethicalengine;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScenarioGenerator {
    private final Random random = new Random();
    private int passengerCountMinimum;
    private int passengerCountMaximum;
    private int pedestrianCountMinimum;
    private int pedestrianCountMaximum;

    public ScenarioGenerator(){
        int randNum = this.random.nextInt();
        this.random.setSeed(randNum);
    }

    public ScenarioGenerator(long seed){
        this.random.setSeed(seed);
    }

    public ScenarioGenerator(long seed, int passengerCountMinimum, int passengerCountMaximum,
                             int pedestrianCountMinimum, int pedestrianCountMaximum){
        this.random.setSeed(seed);
        this.passengerCountMinimum = passengerCountMinimum;
        this.passengerCountMaximum = passengerCountMaximum;
        this.pedestrianCountMinimum = pedestrianCountMinimum;
        this.pedestrianCountMaximum = pedestrianCountMaximum;
    }

    public void setPassengerCountMin(int min){
        this.passengerCountMinimum = min;
    }

    public void setPassengerCountMax(int max){
        this.passengerCountMaximum = max;
    }

    public void setPedestrianCountMin(int min){
        this.pedestrianCountMinimum = min;
    }

    public void setPedestrianCountMax(int max){
        this.pedestrianCountMaximum = max;
    }

    public Person getRandomPerson(){
        int age = this.random.nextInt(100);  // TODO: Check random age upper bound
        Character.Gender[] genderValues = Character.Gender.values();
        Character.Gender gender = genderValues[this.random.nextInt(genderValues.length)];
        Character.BodyType[] bodyTypeValues = Character.BodyType.values();
        Character.BodyType bodyType = bodyTypeValues[this.random.nextInt(bodyTypeValues.length)];
        Person.Profession[] professionValues = Person.Profession.values();
        Person.Profession profession = professionValues[this.random.nextInt(professionValues.length)];
        boolean pregnancy = this.random.nextBoolean();
        return new Person(age, profession, gender, bodyType, pregnancy);
    }

    public Animal getRandomAnimal(){
        // TODO: Check the requirements for randomize an animal
        String[] animals = {"dog", "cat", "bird"};
        String species = animals[random.nextInt(3)];
//        // Randomly generate a string as an animal's name
//        String species = random.ints(97, 122 + 1).limit(5)
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();
        boolean isPet = this.random.nextBoolean();
        Animal animal = new Animal(species);
        animal.setPet(isPet);
        return animal;
    }

    private int getRandNumWithinRange(int min, int max){
        return this.random.nextInt(max - min + 1) + min;
    }

    private Character[] generateCharacters(int numCharacters){
        List<Character> characterList = new ArrayList<>();
        for (int i=0; i<numCharacters; i++){
            if (random.nextBoolean()){
                Person person = getRandomPerson();
                characterList.add(person);
            } else {
                Animal animal = getRandomAnimal();
                characterList.add(animal);
            }
        }
        Character[] characters = new Character[characterList.size()];
        characterList.toArray(characters);
        return characters;
    }

    public void setCharacterNumbers(){
        // TODO: Check random character upper bound
        int passMax = this.random.nextInt(5) + 1; // To avoid the number equals to zero
        int passMin = this.random.nextInt(passMax) + 1;
        int pedesMax = this.random.nextInt(5) + 1;
        int pedesMin = this.random.nextInt(pedesMax) + 1;
        this.setPassengerCountMax(passMax);
        this.setPassengerCountMin(passMin);
        this.setPedestrianCountMax(pedesMax);
        this.setPedestrianCountMin(pedesMin);
    }

    public Scenario generate(){
        if (this.passengerCountMinimum == 0 ||
                this.passengerCountMaximum == 0 ||
                this.pedestrianCountMinimum == 0 ||
                this.pedestrianCountMaximum == 0) {
            this.setCharacterNumbers();
        }
        int numPassengers = getRandNumWithinRange(this.passengerCountMinimum, this.passengerCountMaximum);
        int numPedestrians = getRandNumWithinRange(this.pedestrianCountMinimum, this.pedestrianCountMaximum);
        Character[] passengers = generateCharacters(numPassengers);
        Character[] pedestrians = generateCharacters(numPedestrians);
        boolean isLegal = random.nextBoolean();
        return new Scenario(passengers, pedestrians, isLegal);
    }

    // For test
    public static void main(String[] args) {
        ScenarioGenerator generator = new ScenarioGenerator(25, 1, 5, 1, 5);
        Person person = generator.getRandomPerson();
        System.out.println(person.toString());
        Animal animal = generator.getRandomAnimal();
        System.out.println(animal.toString());
        Scenario scenario = generator.generate();
        System.out.println(scenario.toString());
    }
}
