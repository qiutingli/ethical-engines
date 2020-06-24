package ethicalengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Person extends Character
{
    private Profession profession;
    private boolean isPregnant;
    public boolean isYou;

    enum AgeCategory{BABY, CHILD, ADULT, SENIOR}

    enum Profession{DOCTOR, CEO, CRIMINAL, HOMELESS, UNEMPLOYED, UNKNOWN, NONE}
    
    public Person() {
        this.profession = this.getAgeCategory() == AgeCategory.ADULT? Profession.UNKNOWN : Profession.NONE;
    }
    
    public Person(int age, Profession profession, Gender gender, BodyType bodytype, boolean isPregnant) {
        super(age, gender, bodytype);
        this.profession = this.getAgeCategory() == AgeCategory.ADULT? profession : Profession.NONE;
        this.isPregnant = this.getGender() == Gender.FEMALE && isPregnant;
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
    }
    
    public boolean isYou() {
        return this.isYou;
    }
    
    public void setAsYou(boolean isYou) {
        this.isYou = isYou;
    }
    
    @Override
    public String toString() {
        // [you] <bodyType> <age category> [profession] <gender> [pregnant]
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