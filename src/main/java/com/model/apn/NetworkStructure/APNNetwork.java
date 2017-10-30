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
    private APNNetworkStructure APNNetStructure;
    private Instances instances;
    private APNOutputInfo APNOutputInfoCenter;

    public APNNetwork(Instances instances){
        this.instances = instances;
        this.APNOutputInfoCenter = new APNOutputInfo(instances);
    }

    public void setAPNNetStruct(APNNetworkStructure APNNetStructure){
        this.APNNetStructure = APNNetStructure;
    }

    private void reset(HashMap<Integer, Place> placeMap, HashMap<Integer, Transition> transitionMap){
        placeMap.values().forEach(Place::reset);
        transitionMap.values().forEach(Transition::reset);
    }

    public void setParameters(ArrayList<Double> thresholdList){
        APNNetStructure.setParameters(thresholdList);
    }

    /*
    * Original APN travel
    * */
    public void travel(int curfoldInd){
        HashMap<Integer, Transition> transitionMap = APNNetStructure.getTransitionMap();
        HashMap<Integer, Place> placeMap = APNNetStructure.getPlaceMap();
        HashMap<Integer, Place> rootPlaceMap = new HashMap<>(getRootPlaceMap(placeMap));

        ArrayList<APNOutputInstanceInfo> APNOutputInstanceInfoList = new ArrayList<>(INSTANCE_NUM_TEST);

        IntStream.range(0, INSTANCE_NUM_TEST).forEach(testInstanceInd -> {

            reset(placeMap, transitionMap);

            setAPNNetPlaceInEachTestInstance(placeMap, testInstanceInd);
            setAPNNetSupConf(transitionMap);

            travelProcess(transitionMap);

            APNOutputInstanceInfoList.add(new APNOutputInstanceInfo(testInstanceInd, this.instances)
                    .create(rootPlaceMap, placeMap));

            printTravelResultInfo();
        });

        APNOutputInfoCenter.setAPNOutputInstanceInfo(APNOutputInstanceInfoList, curfoldInd);
    }

    private  void setAPNNetSupConf(HashMap<Integer, Transition> transitionMap){
        transitionMap.values().forEach(Transition::setSupConf);
    }

    /*
    * Bio-APN travel used (speed up)
    * */
    public double getTotalAverageMSE(){
        return APNOutputInfoCenter.calcAverageMSE(getTotalMSE());
    }

    private double getTotalMSE(){
        HashMap<Integer, Transition> transitionMap = APNNetStructure.getTransitionMap();
        HashMap<Integer, Place> placeMap = APNNetStructure.getPlaceMap();
        HashMap<Integer, Place> rootPlaceMap = new HashMap<>(getRootPlaceMap(placeMap));

        //travel
        return IntStream.range(0, INSTANCE_NUM_TRAIN)
                .mapToDouble(trainInstanceInd -> {
                    reset(placeMap, transitionMap);

                    setAPNNetPlaceInEachTrainInstance(placeMap, trainInstanceInd);
                    setAPNNetSupConf(transitionMap);

                    travelProcess(transitionMap);

                    return getInstanceMSE(rootPlaceMap, trainInstanceInd);
                }).sum();
    }

    private double getInstanceMSE(HashMap<Integer, Place> rootPlaceMap, int instanceInd){
        return new APNOutputInstanceInfo(instanceInd, this.instances).getMeanSquaredErrorForBio(rootPlaceMap);
    }

    private  Map<Integer, Place> getRootPlaceMap(HashMap<Integer, Place> placeMap){
        // rootPlaceMap
        return placeMap.entrySet()
                .stream()
                .filter(placeMapItem -> placeMapItem.getValue().getTypeValue() == ROOT_PLACE)
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
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
    }

    private void setAPNNetPlaceInEachTestInstance(HashMap<Integer, Place> placeMap, int testInstanceInd){

        MEPAMembershipMap testEPAMembershipMap = instances.getMEPAMembershipMap(true);

        placeMap.values()
                .stream()
                .peek(place -> place.setTestAttributeValue(testEPAMembershipMap.getAllInstanceByAttr(place.getAttribute()).get(testInstanceInd).getMembership()))
                .filter(place -> place.getTypeValue() == LEAF_PLACE)
                .forEach(place -> {
                    double relationshipDegree = testEPAMembershipMap.getAllInstanceByAttr(place.getAttribute()).get(testInstanceInd).getMembershipDegree();
                    place.setRelationshipDegree(relationshipDegree);
                });

    }

    private void travelProcess(HashMap<Integer, Transition> transitionMap){
        transitionMap.values()
                .stream()
                .sorted((transitionInd, transition) -> transition.getTravelPriority())
                .forEach(Transition::calcInputPlaceRelationshipDegree);
    }

    public APNOutputInfo getAPNOutputInfo(){return this.APNOutputInfoCenter;}

    /*
    * print some process info
    * */
    private void printTravelResultInfo(){
        if(!PRINT_DETAIL_BTN){
            return;
        }

        printTransitionMap();
        printOutputResult();
        printPlaceMap();
    }

    private void printOutputResult(){
        HashMap<Integer, Place> placeMap = APNNetStructure.getPlaceMap();

        System.out.println();
        System.out.println("<---- output result  ---->");
        placeMap.values()
                .stream()
                .filter(place -> place.getTypeValue() == ROOT_PLACE)
                .forEach(place -> System.out.println("class["+place.getRootIndex()+"] "+place.getTestAttributeValue()+" => "+place.getRelationshipDegree()));
    }

    private void printTransitionMap(){
        HashMap<Integer, Transition> transitionMap = APNNetStructure.getTransitionMap();

        System.out.println();
        System.out.println("<---- Transition Info ---->");
        transitionMap.values().forEach(transition -> {
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

    private void printPlaceMap(){
        //Print the route, for explain used

        System.out.println();
        System.out.println("<---- APN travel result: route ---->");
        HashMap<Integer, Place> placeMap = APNNetStructure.getPlaceMap();

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
