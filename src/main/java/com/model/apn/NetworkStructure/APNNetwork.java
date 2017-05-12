package com.model.apn.NetworkStructure;

import Container.MEPAMembershipMap;
import DataStructure.Instances;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static Setup.Config.INSTANCE_NUM_TEST;
import static com.model.apn.Setup.Config.LEAF_PLACE;
import static com.model.apn.Setup.Config.PRINT_TRACETRAVELHISTORY_BTN;
import static com.model.apn.Setup.Config.ROOT_PLACE;

/**
 * Created by JACK on 2017/5/7.
 */
public class APNNetwork {
    APNNetworkStructure APNNetStruct;
    Instances instances;

    public APNNetwork(APNNetworkStructure APNNetStruct, Instances instances){
        this.APNNetStruct = APNNetStruct;
        this.instances = instances;
    }

    public void setParameters(ArrayList<Double> thresholdList){
        APNNetStruct.setParameters(thresholdList);
    }

    public void travel(){
        HashMap<Integer, Transition> transitionMap = APNNetStruct.getTransitionMap();
        HashMap<Integer, Place> placeMap = APNNetStruct.getPlaceMap();

        IntStream.range(0, INSTANCE_NUM_TEST).forEach(testInstanceInd -> {

            setAPNNetPlaceInEachTestInstance(placeMap, testInstanceInd);
            printTracebackTitle();

            transitionMap.values()
                    .stream()
                    .sorted((transitionInd, transition) -> transition.getTravelPriority())
                    .forEach(transition -> transition.calcInputPlaceRelationshipDegree());

            printTransitionMap();
            printPlaceMap();
            reset(placeMap, transitionMap);
        });

    }

    private void setAPNNetPlaceInEachTestInstance(HashMap<Integer, Place> placeMap, int testInstanceInd){

        printTestInstanceInd(testInstanceInd);
        MEPAMembershipMap testEPAMembershipMap = instances.getMEPAMembershipMap(true);

        placeMap.values()
                .stream()
                .peek(place -> place.setTestAttributeValue(testEPAMembershipMap.getAllInstanceByAttr(place.getAttribute()).get(testInstanceInd).getMembership()))
                .filter(place -> place.getTypeValue() == LEAF_PLACE)
                .forEach(place -> {
                    double relationshipDegree = testEPAMembershipMap.getAllInstanceByAttr(place.getAttribute()).get(testInstanceInd).getMembershipDegree();
                    place.setRelationshipDegree(relationshipDegree);
                });

        printPlaceInitInfo(placeMap);
    }

    private void reset(HashMap<Integer, Place> placeMap, HashMap<Integer, Transition> transitionMap){
        placeMap.values()
                .stream()
                .forEach(place -> place.reset());

        transitionMap.values()
                .stream()
                .forEach(transition-> transition.reset());
    }

    private void printTestInstanceInd(int testInstanceInd){
        System.out.println();
        System.out.println("- - - - [ "+testInstanceInd+"'th test instance ] - - - -");
    }

    private void printPlaceInitInfo(HashMap<Integer, Place> placeMap){
        System.out.println();
        System.out.println("<---- Places init Info ---->");
        placeMap.values()
                .stream()
                .forEach(place -> {
                    System.out.print("Member degree = "+place.getRelationshipDegree());
                    if(place.getTypeValue() == ROOT_PLACE){
                        System.out.println(", place"+place.getIndex()+" ("+place.getRootIndex()+") = "+place.getTestAttributeValue());
                    }else {
                        System.out.println(", place"+place.getIndex()+" = "+place.getTestAttributeValue());
                    }
                });
    }

    private void printTracebackTitle(){
        if(!PRINT_TRACETRAVELHISTORY_BTN){
            return;
        }

        System.out.println();
        System.out.println("<---- APN travel traceback ---->");
    }

    private void printTransitionMap(){
        HashMap<Integer, Transition> transitionMap = APNNetStruct.getTransitionMap();

        System.out.println();
        System.out.println("<---- Transition Info ---->");
        transitionMap.values().stream().forEach(transition -> {
            int index = transition.getIndex();
            int priority = transition.getTravelPriority();
            double confidence = transition.getConfidence();
            ArrayList<Double> supportList = transition.getSupport();

            double confThreshold = transition.getConfidenceThreshold();
            ArrayList<Double> supThreshold = transition.getSupportThreshold();

            System.out.println("Transition["+index+"], Priority: "+priority+", Confidence: "+confidence+", Support: "+supportList);
            System.out.println("[Threshold] => Confidence: "+confThreshold+", Support: "+supThreshold);
            System.out.println();
        });
    }

    public void printPlaceMap(){
        //Print the route, for explain used
        System.out.println();
        System.out.println("<---- APN travel result: route ---->");
        HashMap<Integer, Place> placeMap = APNNetStruct.getPlaceMap();

        placeMap.values()
                .stream()
                .filter(place -> place.getTypeValue() == ROOT_PLACE)
                .forEach(place -> {
                    Place curPlace = place;
                    while (!Objects.isNull(curPlace)){

                        if(curPlace.getTypeValue() == ROOT_PLACE){
                            System.out.print("( "+curPlace.getAttribute().getAttributeName()+" ("+curPlace.getTestAttributeValue()+") => "+curPlace.getRelationshipDegree()+") ");
                        }else {
                            System.out.print("( "+curPlace.getAttribute().getAttributeName()+" => "+curPlace.getRelationshipDegree()+") ");
                        }
                        curPlace = curPlace.getMaxRDSelectedInputPlace();
                    }
                    System.out.println();
                });
    }
}
