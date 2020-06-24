package ethicalengine;

public abstract class Character {
    private int age;
    private Character.Gender gender;
    private Character.BodyType bodyType;

    enum Gender{MALE, FEMALE, UNKNOWN}

    enum BodyType{AVERAGE, ATHLETIC, OVERWEIGHT, UNSPECIFIED}
    
    public Character() {
        this.gender = Character.Gender.UNKNOWN;
        this.bodyType = Character.BodyType.UNSPECIFIED;
    }
    
    public Character(final int age, final Character.Gender gender, final Character.BodyType bodyType) {
        this.age = age;
        this.gender = gender;
        this.bodyType = bodyType;
    }
    
    public Character(final Character c) {
        this.age = c.age;
        this.gender = c.gender;
        this.bodyType = c.bodyType;
    }
    
    public int getAge() {
        return this.age;
    }
    
    public Character.Gender getGender() {
        return this.gender;
    }
    
    public Character.BodyType getBodyType() {
        return this.bodyType;
    }
    
    public void setAge(final int age) {
        this.age = age;
    }
    
    public void setGender(final Character.Gender gender) {
        this.gender = gender;
    }
    
    public void setBodyType(final Character.BodyType bodyType) {
        this.bodyType = bodyType;
    }
}