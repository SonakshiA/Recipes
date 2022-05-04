package com.example.recipes;

public class RecipeClass {

    private String recipeName;
    private double fatAmount;

    public RecipeClass(String recipeName, double fatAmount){
        this.recipeName = recipeName;
        this.fatAmount = fatAmount;
    }

    public String getRecipeName(){
        return this.recipeName;
    }

    public double getFatAmount(){
        return this.fatAmount;
    }

}
