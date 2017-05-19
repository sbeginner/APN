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
    private Transition maxRDInputTransition;
    private Place maxRDSelectedInputPlace;
    private HashSet<Transition> inputTransitionSet;
    private HashSet<Transition> outputTransitionSet;
    private Attribute currentAttr;
    private boolean isRoot = false;
    private boolean isLeaf = false;

    private String testAttributeValue = "";

    public void reset(){
        relationshipDegree = NONVALUE_INTEGER;
        testAttributeValue = "";
    }

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

        inputTransitionSet = new HashSet(HALF_ATTRIBUTE_NUM);
        outputTransitionSet = new HashSet(HALF_ATTRIBUTE_NUM);
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

    public String getTestAttributeValue(){
        return this.testAttributeValue;
    }

    public void setRelationshipDegree(double relationshipDegree){
        this.relationshipDegree = relationshipDegree;
    }

    public void setMaxRelationshipDegree(){

        inputTransitionSet.stream()
                .filter(inputTransition -> !inputTransition.checkIsTransitionMinRelationshipDegreeSet())
                .forEach(inputTransition -> inputTransition.calcInputPlaceRelationshipDegree());

        Transition maxTransition = inputTransitionSet.stream()
                .filter(inputTransition -> inputTransition.getRDSelectedInputDegree() > 0)
                .max(Comparator.comparingDouble(inputTransition -> inputTransition.getRDSelectedInputDegree()))
                .orElse(null);

        if(Objects.isNull(maxTransition)){
            //Can't find the satisfied max membership degree from input transitions
            maxRDInputTransition = null;
            maxRDSelectedInputPlace = null;
            setRelationshipDegree(0.0);

            return;
        }

        maxRDInputTransition = maxTransition;
        maxRDSelectedInputPlace = maxTransition.getRDSelectedInputPlace();
        setRelationshipDegree(maxRDInputTransition.getInputDegree());

        printTraceTravel();
    }

    private void printTraceTravel(){
        if(!PRINT_TRACETRAVELHISTORY_BTN){
            return;
        }

        if(isRoot){
            System.out.println(":traceback: "+getTypeName()+" "+currentAttr.getAttributeName()+"["+index+", "+rootIndex+"]"+" => "+relationshipDegree);
        }else {
            System.out.println(":traceback: "+getTypeName()+" "+currentAttr.getAttributeName()+"["+index+"]"+" => "+relationshipDegree);
        }
    }

    private int checkType(){
        if(isLeaf){
            //Leaf
            return LEAF_PLACE;
        }else if(isRoot){
            //Root
            return ROOT_PLACE;
        }else {
            //branch
            return BRANCH_PLACE;
        }
    }

    public int getTypeValue(){
        return checkType();
    }

    public String getTypeName(){
        if(checkType() == LEAF_PLACE){
            //Leaf
            return "LEAF_PLACE";
        }else if(checkType() == ROOT_PLACE){
            //Root
            return "ROOT_PLACE";
        }else {
            //branch
            return "BRANCH_PLACE";
        }
    }

    public void addInputTransitionMap(Transition transition){
        inputTransitionSet.add(transition);
    }

    public void addOutputTransitionMap(Transition transition){
        outputTransitionSet.add(transition);
    }

    public boolean checkIsRelationshipDegreeSet(){
        return relationshipDegree >= 0;
    }

    public double getRelationshipDegree(){
        return relationshipDegree;
    }

    public int getIndex(){
        return index;
    }

    public int getRootIndex(){
        return rootIndex;
    }

    public Attribute getAttribute(){
        return this.currentAttr;
    }

    public Place getMaxRDSelectedInputPlace(){
        return maxRDSelectedInputPlace;
    }

    public HashSet<Transition> getInputTransitionSet(){
        return inputTransitionSet;
    }

    public HashSet<Transition> getOutputTransitionSet(){
        return outputTransitionSet;
    }

    public void test(){
        System.out.println("-------");
        System.out.println("TYPE "+checkType());
        System.out.println("PLACE NAME "+currentAttr.getAttributeName());
        System.out.println("PLACE INDEX "+index+" , "+rootIndex);
        if(rootIndex>=0){
            System.out.println(currentAttr.getAllValue().get(rootIndex));
        }
        System.out.println(inputTransitionSet);
        System.out.println("=>");
        getInputTransitionSet();
        System.out.println(outputTransitionSet);
        System.out.println("=>");
        getOutputTransitionSet();
        System.out.println("-------");
    }

}
