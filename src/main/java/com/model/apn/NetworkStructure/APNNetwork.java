package com.model.apn.NetworkStructure;

import Container.MEPAMembershipMap;
import Container.PriorProbabilityAttr;
import DataStructure.Instances;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;
import com.model.apn.Container.APNOutputInfo;
import com.model.apn.Container.APNOutputInstanceInfo;
import com.model.apn.Eval.ConfusionMatrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.mul;
import static MathCalculate.Arithmetic.sub;
import static Setup.Config.*;
import static com.model.apn.Setup.Config.*;

/**
 * Created by JACK on 2017/5/7.
 */
public class APNNetwork {
    APNNetworkStructure APNNetStruct;
    Instances instances;
    APNOutputInfo APNOutputInfoCenter;

    public APNNetwork(APNNetworkStructure APNNetStruct, Instances instances){
        this.APNNetStruct = APNNetStruct;
        this.instances = instances;

        this.APNOutputInfoCenter = new APNOutputInfo(instances);
    }

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    public void setParameters(ArrayList<Double> thresholdList){
        APNNetStruct.setParameters(thresholdList);
    }

    public void travel(int curfoldInd){
        HashMap<Integer, Transition> transitionMap = APNNetStruct.getTransitionMap();
        HashMap<Integer, Place> placeMap = APNNetStruct.getPlaceMap();
        HashMap<Integer, Place> rootPlaceMap = new HashMap(getRootPlaceMap(placeMap));

        ArrayList<APNOutputInstanceInfo> APNOutputInstanceInfoList = new ArrayList(INSTANCE_NUM_TEST);

        IntStream.range(0, INSTANCE_NUM_TEST).forEach(testInstanceInd -> {

            setAPNNetPlaceInEachTestInstance(placeMap, testInstanceInd);
            setAPNNetSupConf(transitionMap);

            printTracebackTitle();
            travelProcess(transitionMap);

            APNOutputInstanceInfo outputInstanceInfo = getOutputResult(rootPlaceMap, placeMap, testInstanceInd);
            APNOutputInstanceInfoList.add(outputInstanceInfo);

            printTravelResultInfo();

            reset(placeMap, transitionMap);
        });

        APNOutputInfoCenter.setAPNOutputInstanceInfo(APNOutputInstanceInfoList, curfoldInd);
    }

    private  void setAPNNetSupConf(HashMap<Integer, Transition> transitionMap){
        transitionMap.values()
                .stream()
                .forEach(Transition::setSupConf);
    }

    public double getTotalAverageMSE(){
        return APNOutputInfoCenter.calcAverageMSE(getTotalMSE());
    }

    private double getTotalMSE(){
        HashMap<Integer, Transition> transitionMap = APNNetStruct.getTransitionMap();
        HashMap<Integer, Place> placeMap = APNNetStruct.getPlaceMap();
        HashMap<Integer, Place> rootPlaceMap = new HashMap(getRootPlaceMap(placeMap));

        return IntStream.range(0, INSTANCE_NUM_TRAIN)
                .mapToDouble(trainInstanceInd -> {
                    double MSE;
                    setAPNNetPlaceInEachTrainInstance(placeMap, trainInstanceInd);
                    setAPNNetSupConf(transitionMap);
                    travelProcess(transitionMap);
                    MSE = getInstanceMSE(rootPlaceMap, trainInstanceInd);
                    reset(placeMap, transitionMap);
                    return MSE;
                }).sum();
    }

    private double getInstanceMSE(HashMap<Integer, Place> rootPlaceMap, int instanceInd){
        APNOutputInstanceInfo outputInstanceInfo = new APNOutputInstanceInfo(instanceInd, this.instances);
        return outputInstanceInfo.getMeanSquaredErrorForBio(rootPlaceMap);
    }

    private  Map<Integer, Place> getRootPlaceMap(HashMap<Integer, Place> placeMap){
        Map<Integer, Place> rootPlaceMap = placeMap.entrySet()
                .stream()
                .filter(placeMapItem -> placeMapItem.getValue().getTypeValue() == ROOT_PLACE)
                .collect(Collectors.toMap(
                        placeMapItem -> placeMapItem.getKey(),
                        placeMapItem -> placeMapItem.getValue()
                ));
        return rootPlaceMap;
    }

    private void setAPNNetPlaceInEachTrainInstance(HashMap<Integer, Place> placeMap, int trainInstanceInd){

        MEPAMembershipMap trainEPAMembershipMap = instances.getMEPAMembershipMap(false);

        placeMap.values()
                .stream()
                .peek(place -> place.setTestAttributeValue(trainEPAMembershipMap.getAllInstanceByAttr(place.getAttribute()).get(trainInstanceInd).getMembership()))
                .filter(place -> place.getTypeValue() == LEAF_PLACE)
                .forEach(place -> {
                    double relationshipDegree = trainEPAMembershipMap.getAllInstanceByAttr(place.getAttribute()).get(trainInstanceInd).getMembershipDegree();
                    place.setRelationshipDegree(relationshipDegree);
                });

        printPlaceInitInfo(placeMap);
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
                .forEach(Transition::calcInputPlaceRelationshipDegree);
    }


    private APNOutputInstanceInfo getOutputResult(HashMap<Integer, Place> rootPlaceMap, HashMap<Integer, Place> placeMap, int testInstanceInd){
        APNOutputInstanceInfo outputInstanceInfo = new APNOutputInstanceInfo(testInstanceInd, this.instances);
        outputInstanceInfo.create(rootPlaceMap, placeMap);

        return outputInstanceInfo;
    }

    private void reset(HashMap<Integer, Place> placeMap, HashMap<Integer, Transition> transitionMap){
        placeMap.values()
                .stream()
                .forEach(place -> place.reset());

        transitionMap.values()
                .stream()
                .forEach(transition-> transition.reset());
    }

    private void printTravelResultInfo(){
        if(!PRINT_DETAIL_BTN){
            return;
        }

        printTransitionMap();
        printOutputResult();
        printPlaceMap();
    }

    private void printTestInstanceInd(int testInstanceInd){
        if(!PRINT_TRACETRAVELHISTORY_BTN){
            return;
        }

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
        if(!PRINT_DETAIL_BTN){
            return;
        }

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
        if(!PRINT_DETAIL_BTN){
            return;
        }

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
        if(!PRINT_DETAIL_BTN){
            return;
        }

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


    public void getEachConfusionMatrixOutput(){
        APNOutputInfoCenter.getConfusionMatrixMap()
                .values()
                .stream()
                .forEach(confusionMatrix -> confusionMatrix.printConfusionMatrixInfo());
    }

    public void getTotalConfusionMatrixOutput(){
        APNOutputInfoCenter.combineConfusionMatrix();
    }
}
