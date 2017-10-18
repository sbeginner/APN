package com.model.apn.APNObject;

import DataStructure.Instances;
import com.model.apn.Container.hashTransitionInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;

import static MathCalculate.Arithmetic.add;
import static MathCalculate.Arithmetic.mul;
import static com.model.apn.Setup.Config.NONVALUE_INTEGER;
import static com.model.apn.Setup.Config.HALF_ATTRIBUTE_NUM;
import static org.apache.commons.lang3.math.NumberUtils.max;

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
    private hashTransitionInfo hTransitionInfo;

    public Transition(int index, Instances instances){
        this.index = index;
        this.instances = instances;

        inputPlaceSet = new HashSet<>(HALF_ATTRIBUTE_NUM);
        outputPlaceSet = new HashSet<>(HALF_ATTRIBUTE_NUM);
    }

    public void reset(){
        minRDSelectedInputDegree = NONVALUE_INTEGER;
    }

    /*
    * Calculate support and confidence
    */
    public void createRelationship(){
        hashTransitionInfo hTransitionInfo = new hashTransitionInfo(this.instances, this);
        hTransitionInfo.createConfidence();
        hTransitionInfo.createSupport();
        this.hTransitionInfo = hTransitionInfo;
    }

    public void setSupConf(){
        confidence = hTransitionInfo.setConfidence();
        supportSet = hTransitionInfo.setSupport();
//        inputPlaceSet.forEach(i-> System.out.print( "P"+i.getAttribute().getIndex() +" "));
//        System.out.print(" "+index+" "+confidence+" "+supportSet);
//        System.out.println();
    }

    /*
    * Set support and confidence threshold
    */
    public void setThresholdParameters(ArrayList<Double> thresholdList){
        this.confThreshold = thresholdList.get(0);
        this.supThresholdSet = new ArrayList<>(thresholdList.subList(1, thresholdSize));
    }

    public void setThresholdSize(){
        this.thresholdSize = (int)add(inputPlaceSet.size(), 1);//1 for confidence threshold
    }

    public int getThresholdSize(){
        return this.thresholdSize;
    }

    /*
    * The place adds transition and set the transition priority for travel used
    */
    public void addInputPlaceMap(Place place){
        this.inputPlaceSet.add(place);
        this.travelPriority = max(this.travelPriority, place.getTypeValue());
    }

    public void addOutputPlaceMap(Place place){
        this.outputPlaceSet.add(place);
        this.travelPriority = max(this.travelPriority, place.getTypeValue());

        place.addInputTransitionMap(this);
    }

    /*
    *   Main process for traveling (calcInputPlaceRelationshipDegree)
    *   choose the min relationship for this current transition from input places
    */
    public void calcInputPlaceRelationshipDegree(){
        //If the current place doesn't hold any relationship degree(non-value degree: -1), it must to check the previous one satisfies or not,
        //If not, we need to set the value to the previous one first
        //If the previous one is satisfied, then we can set the relationship degree(multiply the confidence) to the current place

        //To ensure the input places have already been traveled
        inputPlaceSet.stream()
                .filter(inputPlace -> inputPlace.getRelationshipDegree() == -1)
                .forEach(Place::setMaxRelationshipDegree);

        Place minPlace = inputPlaceSet.stream()
                .filter(inputPlace -> inputPlace.getRelationshipDegree() > 0)
                .filter(this::checkOverSupportThreshold)
                .filter(inputPlace -> checkOverConfidenceThreshold())
                .min(Comparator.comparingDouble(Place::getRelationshipDegree))
                .orElse(null);

        if(Objects.isNull(minPlace)){
            //Can't find the satisfied min place
            minRDSelectedInputPlace = null;
            minRDSelectedInputDegree = 0.0;
            outputPlaceSet.forEach(Place::setMaxRelationshipDegree);
        }else {
            minRDSelectedInputPlace = minPlace;
            minRDSelectedInputDegree = minPlace.getRelationshipDegree();
            outputPlaceSet.forEach(Place::setMaxRelationshipDegree);
        }
    }

    private boolean checkOverSupportThreshold(Place inputPlace){
        int inputPlaceInd = getInputPlaceIndex(inputPlace);
        return supportSet.get(inputPlaceInd) >= supThresholdSet.get(inputPlaceInd);
    }

    private boolean checkOverConfidenceThreshold(){
        return confidence >= confThreshold;
    }

    private int getInputPlaceIndex(Place inputPlace){
        return new ArrayList<>(inputPlaceSet).indexOf(inputPlace);
    }

    double getTriggerInputDegree(){
        if(triggerCondition()){
            return mul(minRDSelectedInputDegree, confidence);
        }
        return 0.0;
    }

    private boolean triggerCondition(){
        return confidence >= confThreshold;
    }

    public int getIndex(){
        return index;
    }

    public HashSet<Place> getInputPlaceSet(){
        return inputPlaceSet;
    }

    public HashSet<Place> getOutputPlaceSet(){
        return outputPlaceSet;
    }

    Place getRDSelectedInputPlace(){
        return minRDSelectedInputPlace;
    }

    double getRDSelectedInputDegree(){
        return minRDSelectedInputDegree;
    }

    boolean checkIsTransitionMinRelationshipDegreeSet(){
        return minRDSelectedInputDegree >= 0;
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
