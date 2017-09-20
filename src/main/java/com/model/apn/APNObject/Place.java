package com.model.apn.APNObject;


import DataStructure.Attribute;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;

import static com.model.apn.Setup.Config.*;

/**
 * Created by JACK on 2017/5/5.
 */
public class Place {
    private int index = NONVALUE_INTEGER;
    private int rootIndex = NONVALUE_INTEGER;
    private double relationshipDegree = NONVALUE_INTEGER;
    private Place maxRDSelectedInputPlace;
    private HashSet<Transition> inputTransitionSet;
    private Attribute currentAttr;
    private boolean isRoot = false;
    private boolean isLeaf = false;
    private String testAttributeValue = "";

    public Place(Attribute currentAttr){
        init(currentAttr);
    }

    public Place(Attribute currentAttr, int rootIndex){
        this.rootIndex = rootIndex;
        init(currentAttr);
    }

    private void init(Attribute currentAttr){
        this.index = currentAttr.getIndex();
        this.currentAttr = currentAttr;

        inputTransitionSet = new HashSet<>(HALF_ATTRIBUTE_NUM);
    }

    /*
    *   Main process for traveling (setMaxRelationshipDegree)
    *   choose the max relationship for this current place from input transitions
    */
    void setMaxRelationshipDegree(){
        //To ensure the input transitions are already held the value
        inputTransitionSet.stream()
                .filter(inputTransition -> !inputTransition.checkIsTransitionMinRelationshipDegreeSet())
                .forEach(Transition::calcInputPlaceRelationshipDegree);

        //Choose the max-value transition, and hold the value in this place
        Transition maxTransition = inputTransitionSet.stream()
                .filter(inputTransition -> inputTransition.getRDSelectedInputDegree() > 0)
                .max(Comparator.comparingDouble(Transition::getRDSelectedInputDegree))
                .orElse(null);

        if(Objects.isNull(maxTransition)){
            //Can't find the satisfied max membership degree from input transitions
            maxRDSelectedInputPlace = null;
            setRelationshipDegree(0.0);
        }else{
            maxRDSelectedInputPlace = maxTransition.getRDSelectedInputPlace();
            setRelationshipDegree(maxTransition.getTriggerInputDegree());
        }
    }

    public void reset(){
        relationshipDegree = NONVALUE_INTEGER;
        testAttributeValue = "";
    }

    public void isRootPlace(){
        isRoot = true;
    }

    public void isLeafPlace(){
        isLeaf = true;
    }

    public void setTestAttributeValue(String testAttributeValue){
        if(isRoot){
            this.testAttributeValue = currentAttr.getAllValue().get(rootIndex).toString();//attribute value must be discrete value
            return;
        }

        this.testAttributeValue = testAttributeValue;
    }

    public void setRelationshipDegree(double relationshipDegree){
        this.relationshipDegree = relationshipDegree;
    }

    public int getTypeValue(){
        if(isLeaf){
            return LEAF_PLACE;
        }else if(isRoot){
            return ROOT_PLACE;
        }else {
            return BRANCH_PLACE;
        }
    }

    public int getRootIndex(){
        return this.rootIndex;
    }

    public double getRelationshipDegree(){
        return this.relationshipDegree;
    }

    public String getTestAttributeValue(){
        return this.testAttributeValue;
    }

    public Attribute getAttribute(){
        return this.currentAttr;
    }

    public Place getMaxRDSelectedInputPlace(){
        return this.maxRDSelectedInputPlace;
    }

    void addInputTransitionMap(Transition transition){
        this.inputTransitionSet.add(transition);
    }
}
