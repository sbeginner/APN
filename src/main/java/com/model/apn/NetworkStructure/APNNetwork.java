package com.model.apn.NetworkStructure;

import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.model.apn.Setup.Config.ROOT_PLACE;

/**
 * Created by JACK on 2017/5/7.
 */
public class APNNetwork {
    APNNetworkStructure APNNetStruct;
    public APNNetwork(APNNetworkStructure APNNetStruct){
        this.APNNetStruct = APNNetStruct;
    }

    public void setParameters(ArrayList<Double> thresholdList){
        APNNetStruct.setParameters(thresholdList);
    }

    public void travel(){
        HashMap<Integer, Transition> transitionMap = APNNetStruct.getTransitionMap();

        transitionMap.values()
                .stream()
                .sorted((transitionInd, transition) -> transition.getTravelPriority())
                .forEach(transition -> transition.calcInputPlaceRelationshipDegree());
    }


    public void printPlaceMap(){
        //Print the route, for explain used
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
