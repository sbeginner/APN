package com.model.apn.APNObject;

import MathCalculate.Arithmetic;

import java.util.Comparator;
import java.util.HashSet;

import static com.model.apn.Setup.Config.NONVALUE_INTEGER;
import static com.model.apn.Setup.Config.HALF_ATTRIBUTE_NUM;

/**
 * Created by JACK on 2017/5/5.
 */
public class Transition {
    private int index = NONVALUE_INTEGER;
    private double minRDSelectedInputDegree = NONVALUE_INTEGER;
    private HashSet<Place> inputPlaceSet;
    private HashSet<Place> outputPlaceSet;
    private double confidence = 1.1;
    private Place minRDSelectedInputPlace;


    public Transition(int index){
        this.index = index;

        inputPlaceSet = new HashSet(HALF_ATTRIBUTE_NUM);
        outputPlaceSet = new HashSet(HALF_ATTRIBUTE_NUM);
    }

    public void addInputPlaceMap(Place place){
        inputPlaceSet.add(place);
        place.addOutputTransitionMap(this);
    }

    public void addOutputPlaceMap(Place place){
        outputPlaceSet.add(place);
        place.addInputTransitionMap(this);
    }

    public void calcInputPlaceRelationshipDegree(){

        inputPlaceSet.stream().forEach(inputPlace -> {
            if(!inputPlace.checkIsRelationshipDegreeSet()){
                HashSet<Transition> curPlaceInputTrasition = inputPlace.getInputTransitionSet();
                curPlaceInputTrasition.stream().forEach(Transition::calcInputPlaceRelationshipDegree);
            }
        });

        Place minPlace = inputPlaceSet.stream()
                .filter(inputPlace -> inputPlace.getRelationshipDegree() > 0)
                .min(Comparator.comparingDouble(inputPlace -> inputPlace.getRelationshipDegree()))
                .orElse(inputPlaceSet.iterator().next());

        minRDSelectedInputPlace = minPlace;
        minRDSelectedInputDegree = minPlace.getRelationshipDegree();

        outputPlaceSet.stream().forEach(outputPlace -> outputPlace.setMaxRelationshipDegree());
    }

    public int getIndex(){
        System.out.println(index);
        return index;
    }

    public Place getRDSelectedInputPlace(){
        return minRDSelectedInputPlace;
    }

    public double getRDSelectedInputDegree(){
        return minRDSelectedInputDegree;
    }

    public double getActiveInputDegree(){
        return Arithmetic.mul(minRDSelectedInputDegree, confidence);
    }

    public void getInputPlaceSet(){
        inputPlaceSet.forEach(Place::test);
    }

    public void getOutputPlaceSet(){
        outputPlaceSet.forEach(Place::test);
    }
}
