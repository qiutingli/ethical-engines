package ethicalengine;

import java.util.ArrayList;
import java.util.List;

public class Animal extends Character{
    private String species;
    private boolean isPet;

    public Animal(String species){
        this.species = species;
    }

    public Animal(Animal otherAnimal){
        this.species = otherAnimal.species;
        this.isPet = otherAnimal.isPet;
    }

    public String getSpecies(){
        return this.species;
    }

    public void setSpecies(String species){
        this.species = species;
    }

    public boolean isPet(){
        return this.isPet;
    }

    public void setPet(Boolean isPet){
        this.isPet = isPet;
    }

    @Override
    public String toString() {
//        <species> [is pet]
        List<String> summaryList = new ArrayList<>();
        summaryList.add(this.species);
        if (this.isPet){
            summaryList.add("is pet");
        }
        String summary = String.join(" ", summaryList);
        return summary.toLowerCase();
    }

    public static void main(String[] args) {
        Animal animal = new Animal("cat");
        animal.setPet(true);
        Animal animal1 = new Animal(animal);
        animal1.setSpecies("bird");
        animal1.setPet(false);
        System.out.println(animal.toString());
        System.out.println(animal1.toString());
    }
}
