package com.model.apn.APNObject;

import DataStructure.Instances;

import java.util.*;

import static MathCalculate.Arithmetic.add;
import static MathCalculate.Arithmetic.mul;
import static com.model.apn.Setup.Config.NONVALUE_INTEGER;
import static com.model.apn.Setup.Config.HALF_ATTRIBUTE_NUM;
import static org.apache.commons.lang3.math.NumberUtils.max;
import static org.apache.commons.lang3.math.NumberUtils.min;

/**
 * Created by JACK on 2017/5/5.
 */
public class Transition {
    private Instances instances;

    private int travelPriority = NONVALUE_INTEGER; //For travel used
    private int index = NONVALUE_INTEGER;
    private int thresholdSize = NONVALUE_INTEGER;
    private double minRDSelectedInputDegree = NONVALUE_INTEGER;
    private HashSet<Place> inputPlaceSet;
    private HashSet<Place> outputPlaceSet;
    private Place minRDSelectedInputPlace;

    private double confidence = NONVALUE_INTEGER;
    private ArrayList<Double> supportSet;

    private double confThreshold =  0.1;
    private ArrayList<Double> supThresholdSet;

    public Transition(int index, Instances instances){
        this.index = index;
        this.instances = instances;

        inputPlaceSet = new HashSet(HALF_ATTRIBUTE_NUM);
        outputPlaceSet = new HashSet(HALF_ATTRIBUTE_NUM);
    }

    public void reset(){
        minRDSelectedInputDegree = NONVALUE_INTEGER;
    }

    public void createRelationship(){
        System.out.println(inputPlaceSet.hashCode());
        inputPlaceSet.stream().forEach(i -> System.out.println( i.getIndex()+" "+i.getRootIndex()));
        System.out.println(outputPlaceSet.hashCode());
        outputPlaceSet.stream().forEach(i -> System.out.println( i.getIndex()+" "+i.getRootIndex()));
        System.out.println();
        createConfidence();
        createSupport();
    }

    private void createConfidence(){

    }

    private void createSupport(){

    }

    private void setParametersConfidenceThreshold(double confThreshold){
        this.confThreshold = confThreshold;
    }

    private void setParametersSupportThreshold(ArrayList<Double> supThresholdSet){
        this.supThresholdSet = supThresholdSet;
    }

    public void setParameters(ArrayList<Double> thresholdList){
        setParametersConfidenceThreshold(thresholdList.get(0));
        setParametersSupportThreshold(new ArrayList(thresholdList.subList(1, thresholdSize)));
    }

    private void setSupport(){
        supportSet = new ArrayList(Collections.nCopies(inputPlaceSet.size(), 0.2));
        //calc support
    }

    private void setConfidence(){
        confidence = 0.9;
        //calc confidence

    }

    public void setSupConf(){
        setConfidence();
        setSupport();
    }

    public int setThresholdSize(){
        this.thresholdSize = (int)add(inputPlaceSet.size(), 1);//1 for confidence threshold
        return this.thresholdSize;
    }

    public int getThresholdSize(){
        return this.thresholdSize;
    }

    public void addInputPlaceMap(Place place){
        inputPlaceSet.add(place);
        place.addOutputTransitionMap(this);

        this.travelPriority = max(this.travelPriority, place.getTypeValue());
    }

    public void addOutputPlaceMap(Place place){
        outputPlaceSet.add(place);
        place.addInputTransitionMap(this);

        this.travelPriority = this.travelPriority + place.getTypeValue();
    }

    public void calcInputPlaceRelationshipDegree(){
        //If the current place doesn't hold any relationship degree(non-value degree: -1),
        //it must to check the previous one satisfies or not,
        //if not, we need to set the value to the previous one first
        //if the previous one is satisfied, then we can set the relationship degree(multiply the confidence) to the current place
        inputPlaceSet.stream()
                .filter(inputPlace -> !inputPlace.checkIsRelationshipDegreeSet())
                .forEach(inputPlace -> {
                    HashSet<Transition> curPlaceInputTrasition = inputPlace.getInputTransitionSet();
                    curPlaceInputTrasition.stream().forEach(Transition::calcInputPlaceRelationshipDegree);
                });

        Place minPlace = inputPlaceSet.stream()
                .filter(inputPlace -> inputPlace.getRelationshipDegree() > 0)
                .filter(inputPlace -> checkOverSupportThreshold(inputPlace))
                .filter(inputPlace -> checkOverConfidenceThreshold())
                .min(Comparator.comparingDouble(inputPlace -> inputPlace.getRelationshipDegree()))
                .orElse(null);

        if(Objects.isNull(minPlace)){
            //Can't find the satisfied min place
            minRDSelectedInputPlace = null;
            minRDSelectedInputDegree = 0.0;
            outputPlaceSet.stream()
                    .forEach(outputPlace -> outputPlace.setMaxRelationshipDegree());

            return;
        }

        minRDSelectedInputPlace = minPlace;
        minRDSelectedInputDegree = minPlace.getRelationshipDegree();
        outputPlaceSet.stream()
                .forEach(outputPlace -> outputPlace.setMaxRelationshipDegree());
    }

    private boolean checkOverSupportThreshold(Place inputPlace){
        int inputPlaceInd = getInputPlaceIndex(inputPlace);
        return supportSet.get(inputPlaceInd) >= supThresholdSet.get(inputPlaceInd);
    }

    private boolean checkOverConfidenceThreshold(){
        return confidence >= confThreshold;
    }

    private int getInputPlaceIndex(Place inputPlace){
        return new ArrayList(inputPlaceSet).indexOf(inputPlace);
    }

    public int getIndex(){
        return index;
    }

    public HashSet<Place> getInputPlaceSet(){
        return inputPlaceSet;
    }

    public Place getRDSelectedInputPlace(){
        return minRDSelectedInputPlace;
    }

    public double getRDSelectedInputDegree(){
        return minRDSelectedInputDegree;
    }

    public boolean checkIsTransitionMinRelationshipDegreeSet(){
        return minRDSelectedInputDegree >= 0;
    }


    public double getActiveInputDegree(){
        return mul(minRDSelectedInputDegree, confidence);
    }

    public double getInActiveInputDegree(){
        return 0.0;
    }

    public double getInputDegree(){
        if(confidence >= confThreshold){
            return getActiveInputDegree();
        }

        return getInActiveInputDegree();
    }

    public int getTravelPriority(){
        return travelPriority;
    }

    public double getConfidence(){
        return confidence;
    }

    public ArrayList<Double> getSupport(){
        return supportSet;
    }

    public double getConfidenceThreshold(){
        return confThreshold;
    }

    public ArrayList<Double> getSupportThreshold(){
        return supThresholdSet;
    }
}
