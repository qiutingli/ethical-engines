package ethicalengine;

import java.util.List;
import java.util.Arrays;

public class Person extends Character
{
    private Person.Profession profession;
    private boolean isPregnant;
    private boolean isYou;

    enum AgeCategory{BABY, CHILD, ADULT, SENIOR}

    enum Profession{DOCTOR, CEO, CRIMINAL, HOMELESS, UNEMPLOYED, UNKNOWN}
    
    public Person() {
        this.profession = null;
    }
    
    public Person(final int age, final Person.Profession profession, final Character.Gender gender, final Character.BodyType bodytype, final boolean isPregnant) {
        super(age, gender, bodytype);
        this.profession = null;
        this.profession = profession;
        this.isPregnant = isPregnant;
    }
    
    public Person(final Person otherPerson) {
        super(otherPerson);
        this.profession = null;
        this.profession = otherPerson.profession;
        this.isPregnant = otherPerson.isPregnant;
    }
    
    public static boolean isBetween(final int age, final int lower, final int upper) {
        return lower <= age && age <= upper;
    }
    
    public Person.AgeCategory getAgeCategory() {
        final int age = this.getAge();
        if (isBetween(age, 0, 4)) {
            return Person.AgeCategory.BABY;
        }
        if (isBetween(age, 5, 16)) {
            return Person.AgeCategory.CHILD;
        }
        if (isBetween(age, 17, 68)) {
            return Person.AgeCategory.ADULT;
        }
        if (age > 68) {
            return Person.AgeCategory.SENIOR;
        }
        throw new IllegalArgumentException("Invalid age");
    }
    
    public Person.Profession getProfession() {
        if (this.getAgeCategory() == Person.AgeCategory.ADULT) {
            return this.profession;
        }
        return null;
    }
    
    public boolean isPregnant() {
        return this.getGender() == Character.Gender.FEMALE && this.isPregnant;
    }
    
    public void setPregnant(final boolean pregnant) {
        if (this.getGender() == Character.Gender.FEMALE) {
            this.isPregnant = pregnant;
        }
        else {
            System.out.println("Only a female can set pregnant!");
        }
    }
    
    public boolean isYou() {
        return this.isYou;
    }
    
    public void setAsYou(final boolean isYou) {
        this.isYou = isYou;
    }
    
    @Override
    public String toString() {
        final List<String> words = Arrays.asList(String.valueOf(this.isYou), String.valueOf(this.getBodyType()), String.valueOf(this.getAgeCategory()), String.valueOf(this.getProfession()), String.valueOf(this.getGender()), String.valueOf(this.isPregnant));
        final String output = String.join(" ", words);
        return output.toLowerCase();
    }
    
    public static void main(final String[] args) {
        final Person person = new Person();
        final Person person2 = new Person(person);
        System.out.println(person2.getGender());
        System.out.println(person.getAgeCategory());
        System.out.println(person.getProfession());
        System.out.println(person.isPregnant);
        System.out.println(person.toString());
    }
}