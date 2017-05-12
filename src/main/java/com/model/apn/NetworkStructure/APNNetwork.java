package com.model.apn.NetworkStructure;

import Container.MEPAMembershipMap;
import Container.PriorProbabilityAttr;
import DataStructure.Instances;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.mul;
import static MathCalculate.Arithmetic.sub;
import static Setup.Config.INSTANCE_NUM_TEST;
import static Setup.Config.TARGET_ATTRIBUTE;
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
            travelProcess(transitionMap);

            getOutputResult(placeMap, testInstanceInd);



            printTransitionMap();
            printOutputResult();
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

    private void travelProcess(HashMap<Integer, Transition> transitionMap){
        transitionMap.values()
                .stream()
                .sorted((transitionInd, transition) -> transition.getTravelPriority())
                .forEach(transition -> transition.calcInputPlaceRelationshipDegree());
    }

    private void getOutputResult(HashMap<Integer, Place> placeMap, int testInstanceInd){

        ArrayList<String> attributeValueList = instances.getMEPAMembershipMap(false).getAttributeValue(TARGET_ATTRIBUTE);
        Iterator iterator = attributeValueList.iterator();
        double maxValue = -1;
        String maxTargetValue = "";
        while (iterator.hasNext()){
            String curTargetValue = iterator.next().toString();

            double targetPriorProbability = placeMap.values()
                    .stream()
                    .filter(place -> place.getTypeValue() != ROOT_PLACE)
                    .mapToDouble(place -> {
                        MEPAMembershipMap trainMEPAMembershipMap  = instances.getMEPAMembershipMap(false);
                        PriorProbabilityAttr ppAttr = trainMEPAMembershipMap.getPriorProbabilityValueByAttr(place.getAttribute());
                        //System.out.println(place.getTestAttributeValue()+" "+curTargetValue+" "+ppAttr.getProbabilityByAttributeValue(place.getTestAttributeValue(), curTargetValue));
                        return ppAttr.getProbabilityByAttributeValue(place.getTestAttributeValue(), curTargetValue);
                    })
                    .reduce(1, (totalNum, curNum) -> mul(totalNum, curNum));

            System.out.println(curTargetValue+" "+targetPriorProbability);

            if(targetPriorProbability > maxValue){
                maxTargetValue = curTargetValue;
                maxValue = targetPriorProbability;
            }
        }

        System.out.println(maxTargetValue+" "+maxValue);

        Place maxPlace = placeMap.values()
                .stream()
                .filter(place -> place.getTypeValue() == ROOT_PLACE)
                .max(Comparator.comparing(Place::getRelationshipDegree))
                .orElse(null);
        System.out.println(maxPlace.getRelationshipDegree()+" "+maxPlace.getTestAttributeValue());

        placeMap.values()
                .stream()
                .filter(place -> place.getTypeValue() == ROOT_PLACE)
                .forEach(place -> System.out.println("class["+place.getRootIndex()+"] "+place.getTestAttributeValue()+" => "+place.getRelationshipDegree()));

        String curTestInstanceIndTarget = instances.getMEPAMembershipMap(true)
                .getAllInstanceByAttr(TARGET_ATTRIBUTE)
                .get(testInstanceInd)
                .getMembership();

        System.out.println(curTestInstanceIndTarget);

        double a = placeMap.values()
                .stream()
                .filter(place -> place.getTypeValue() == ROOT_PLACE)
                .mapToDouble(place -> {
                    if(curTestInstanceIndTarget.equals(place.getTestAttributeValue())){
                        return mul(sub(1, place.getRelationshipDegree()), sub(1, place.getRelationshipDegree()));
                    }else {
                        return mul(sub(0, place.getRelationshipDegree()), sub(0, place.getRelationshipDegree()));
                    }
                }).sum();
        System.out.println(a);
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

    private void printOutputResult(){
        HashMap<Integer, Place> placeMap = APNNetStruct.getPlaceMap();

        System.out.println();
        System.out.println("<---- output result  ---->");
        placeMap.values()
                .stream()
                .filter(place -> place.getTypeValue() == ROOT_PLACE)
                .forEach(place -> System.out.println("class["+place.getRootIndex()+"] "+place.getTestAttributeValue()+" => "+place.getRelationshipDegree()));
    }

    private void printTracebackTitle(){
        if(!PRINT_TRACETRAVELHISTORY_BTN){
            return;
        }

        System.out.println();
        System.out.println("<---- APN travel traceback ---->");
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
