package ethicalengine;

public class Person extends Character {
    private Profession profession;
    private boolean isPregnant;
    public Person(int age, Profession profession, Gender gender, BodyType bodyType, boolean isPregnant){
        this.age = age;
        this.profession = profession;
        this.gender = gender;
        this.bodyType = bodyType;
        this.isPregnant = isPregnant;
    }

    public Person(Person otherPerson){

    }
    enum AgeCategory {
        BABY,
        CHILD,
        ADULT,
        SENIOR}

    public AgeCategory getAgeCategory(){
        if ((this.age>=0)&&(this.age<=4)){
            return AgeCategory.BABY;
        }
        else if ((this.age>=5)&&(this.age<=16)){
            return AgeCategory.CHILD;
        }
        else if ((this.age>=17)&&(this.age<=68)){
            return AgeCategory.ADULT;
        }
        else{
            return AgeCategory.SENIOR;
        }
    }

    enum Profession{
        DOCTOR,
        CEO,
        CRIMINAL,
        HOMELESS,
        UNEMPLOYED,
        UNKNOWN
    }
    public Profession getProfession(){
        if((this.age>=17)&&(this.age<=68)){
            return 
        }else{
            return
        }
    }

    public boolean isPregnant(){

    }

    public void setPregnant(boolean isPregnant){

    }

    public boolean isYou(){

    }

    public void setAsYou(boolean isYou){

    }

    public

}
