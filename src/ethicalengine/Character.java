package ethicalengine;

public abstract class Character {
    private int age;
    private Gender gender;
    private BodyType bodyType;

    enum Gender{MALE, FEMALE, UNKNOWN}

    enum BodyType{AVERAGE, ATHLETIC, OVERWEIGHT, UNSPECIFIED}
    
    public Character() {
        this.age = 0;
        this.gender = Gender.UNKNOWN;
        this.bodyType = BodyType.UNSPECIFIED;
    }
    
    public Character(int age, Gender gender, BodyType bodyType) {
        this.age = Math.max(age, 0);
        this.gender = gender;
        this.bodyType = bodyType;
    }
    
    public Character(Character c) {
        this.age = c.age;
        this.gender = c.gender;
        this.bodyType = c.bodyType;
    }
    
    public int getAge() {
        return this.age;
    }
    
    public Gender getGender() {
        return this.gender;
    }
    
    public BodyType getBodyType() {
        return this.bodyType;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }
}