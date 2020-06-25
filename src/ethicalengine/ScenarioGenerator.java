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
        int age = this.random.nextInt(120);
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
//        TODO: Check the requirements for randomize ann animal
//        byte[] array = new byte[5];
//        this.random.nextBytes(array);
//        String species = new String(array, Charset.forName("UTF-8"));
        String species = random.ints(97, 122 + 1).limit(5)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
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

    public Scenario generate(){
        int numPassengers = getRandNumWithinRange(this.passengerCountMinimum, this.passengerCountMaximum);
        int numPedestrians = getRandNumWithinRange(this.pedestrianCountMinimum, this.pedestrianCountMaximum);
        Character[] passengers = generateCharacters(numPassengers);
        Character[] pedestrians = generateCharacters(numPedestrians);
        boolean isLegal = random.nextBoolean();
        return new Scenario(passengers, pedestrians, isLegal);
    }

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
