package ethicalengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Person extends Character
{
    private Profession profession;
    private boolean isPregnant;
    private boolean isYou;

    enum AgeCategory{BABY, CHILD, ADULT, SENIOR}

    enum Profession{DOCTOR, CEO, CRIMINAL, HOMELESS, UNEMPLOYED, UNKNOWN, NONE}
    
    public Person() {
        if (this.getAgeCategory() == AgeCategory.ADULT){
            this.profession = Profession.UNKNOWN;
        } else {
            this.profession = Profession.NONE;
        }
    }
    
    public Person(int age, Profession profession, Gender gender, BodyType bodytype, boolean isPregnant) {
        super(age, gender, bodytype);
        if (this.getAgeCategory() == AgeCategory.ADULT){
            this.profession = profession;
        } else {
            this.profession = Profession.NONE;
        }
        if (this.getGender() == Gender.FEMALE) {
            this.isPregnant = isPregnant;
        } else {
            System.out.println("Only a female can set pregnant!");
        }
    }
    
    public Person(Person otherPerson) {
        super(otherPerson);
        this.profession = otherPerson.profession;
        this.isPregnant = otherPerson.isPregnant;
        this.isYou = otherPerson.isYou;
    }
    
    public static boolean isBetween(int age, int lower, int upper) {
        return lower <= age && age <= upper;
    }
    
    public AgeCategory getAgeCategory() {
        int age = this.getAge();
        if (isBetween(age, 0, 4)) {
            return AgeCategory.BABY;
        } else if (isBetween(age, 5, 16)) {
            return AgeCategory.CHILD;
        } else if (isBetween(age, 17, 68)) {
            return AgeCategory.ADULT;
        } else if (age > 68) {
            return AgeCategory.SENIOR;
        }
        throw new IllegalArgumentException("Invalid age");
    }
    
    public Profession getProfession() {
        if (this.getAgeCategory() == AgeCategory.ADULT) {
            return this.profession;
        }
        return Profession.NONE;
    }
    
    public boolean isPregnant() {
        return this.getGender() == Gender.FEMALE && this.isPregnant;
    }
    
    public void setPregnant(boolean pregnant) {
        if (this.getGender() == Gender.FEMALE) {
            this.isPregnant = pregnant;
        }
        else {
            System.out.println("Only a female can be set to be pregnant!");
        }
    }
    
    public boolean isYou() {
        return this.isYou;
    }
    
    public void setAsYou(boolean isYou) {
        this.isYou = isYou;
    }
    
    @Override
    public String toString() {
//        [you] <bodyType> <age category> [profession] <gender> [pregnant]
        List<String> summaryList = new ArrayList<>();
        if (this.isYou){
            summaryList.add("you");
        }
        summaryList.add(String.valueOf(this.getBodyType()));
        summaryList.add(String.valueOf(this.getAgeCategory()));
        if (this.profession != Profession.NONE && this.profession != null){
            summaryList.add(String.valueOf(this.getProfession()));
        }
        summaryList.add(String.valueOf(String.valueOf(this.getGender())));
        if (this.isPregnant){
            summaryList.add("pregnant");
        }
        String summary = String.join(" ", summaryList);
        return summary.toLowerCase();
    }
    
    public static void main(String[] args) {
        Person person = new Person(27, Profession.DOCTOR, Gender.MALE, BodyType.ATHLETIC, true);
        person.setAsYou(true);
        Person person1 = new Person(person);
        System.out.println("Gender: " + person1.getGender());
        System.out.println("Age Category: " + person.getAgeCategory());
        System.out.println("Profession: " + person.getProfession());
        System.out.println("Pregnancy: " + person.isPregnant);
        System.out.println("Person summary: " + person.toString());
        System.out.println("Person copy summary: " + person1.toString());
    }
}