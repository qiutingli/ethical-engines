package ethicalengine;

public class Character {
    private int age;
    private Gender gender;
    private Bodytype bodyType;

    enum Gender{
        FEMALE,
        MALE,
        UNKNOWN}

    enum Bodytype{
        AVERAGE,
        ATHLETIC,
        OVERWEIGHT,
        UNSPECIFIED}

    public Character(){
        this.gender = Gender.UNKNOWN;
        this.bodyType = Bodytype.UNSPECIFIED;

    }

    public Character(int age, Gender gender, Bodytype bodyType) {
        this.age = age;
        this.gender = gender;
        this.bodyType = bodyType;
    }

    public Character(Character c ){

    }

    public int getAge(){
        return this.age;
    }

    public Gender getGender(){
        return this.gender;
    }

    public Bodytype getBodyType(){
        return this.bodyType;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setGender(Gender gender){
        this.gender = gender;
    }

    public void setBodyType(Bodytype bodyType){
        this.bodyType = bodyType;
    }


}
