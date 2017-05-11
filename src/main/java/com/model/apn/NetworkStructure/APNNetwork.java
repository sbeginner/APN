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

        if(PRINT_TRACETRAVELHISTORY_BTN){
            System.out.println();
            System.out.println("<---- APN travel traceback ---->");
        }

        IntStream.range(0, INSTANCE_NUM_TEST).forEach(testInstanceInd -> {
            System.out.println(testInstanceInd+">>");
            setAPNNetPlaceInEachTestInstance(placeMap, testInstanceInd);

            transitionMap.values()
                    .stream()
                    .sorted((transitionInd, transition) -> transition.getTravelPriority())
                    .forEach(transition -> transition.calcInputPlaceRelationshipDegree());

            System.out.println();


            printPlaceMap();
            reset(placeMap, transitionMap);
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

        placeMap.values().stream().forEach(p -> System.out.println(p.getRelationshipDegree()+", "+p.getTestAttributeValue()) );
    }

    private void reset(HashMap<Integer, Place> placeMap, HashMap<Integer, Transition> transitionMap){
        placeMap.values()
                .stream()
                .forEach(place -> place.reset());

        transitionMap.values()
                .stream()
                .forEach(transition-> transition.reset());
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
                        System.out.print("( "+curPlace.getAttribute().getAttributeName()+","+curPlace.getRootIndex()+" => "+curPlace.getRelationshipDegree()+") ");
                        curPlace = curPlace.getMaxRDSelectedInputPlace();
                    }
                    System.out.println();
                });
    }
}
