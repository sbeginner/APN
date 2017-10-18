package com.model.apn.NetworkStructure;

import DataStructure.Instances;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;
import com.model.apn.NetworkStructureTemplate.CorrelationNetwork;
import java.util.*;
import java.util.stream.IntStream;
import static MathCalculate.Arithmetic.sub;
import static com.model.apn.Setup.Config.ATTRIBUTE_NUM;
import static com.model.apn.Setup.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Setup.Config.THRESHOLD_NUM;
/**
 * Created by JACK on 2017/5/5.
 */
public class APNNetworkStructure {
    private int FEATUREPLACENUM, ALLPLACENUM;
    private Instances instances;
    private HashMap<Integer, Transition> transitionMap;
    private HashMap<Integer, Place> placeMap;
    private int[][] networkStructure;

    public APNNetworkStructure(Instances instances){
        this.instances = instances;
        init();
    }

    HashMap<Integer, Transition> getTransitionMap(){
        return transitionMap;
    }

    HashMap<Integer, Place> getPlaceMap(){
        return placeMap;
    }

    private void init(){
        FEATUREPLACENUM = ATTRIBUTE_NUM - 1;
        ALLPLACENUM = FEATUREPLACENUM + instances.getAttribute(TARGET_ATTRIBUTE).getAllValue().size();
    }

    private HashMap<Integer, Place> createPlace(int[][] networkStructure){
        //To create the places (all attributes)
        HashMap<Integer, Place> placeMaptmp = new HashMap(ALLPLACENUM);
        IntStream.range(0, networkStructure.length)
                .forEach(nRow -> {
                    Place pp;
                    if(nRow >= TARGET_ATTRIBUTE){
                        //Set the root places(multi-target value)
                        pp = new Place(instances.getAttribute(TARGET_ATTRIBUTE), (int)sub(nRow, TARGET_ATTRIBUTE));
                        pp.isRootPlace();
                    }else {
                        //Set the general places(also named attribute, pattern,and so on...)
                        pp = new Place(instances.getAttribute(nRow));
                        if(!Arrays.stream(networkStructure[nRow]).filter(transitionInd -> (transitionInd >= 0)).findAny().isPresent()){
                            //To check the n'th row doesn't hold any transition Index, then we call this leaf place
                            pp.setRelationshipDegree(0.1 * (nRow+1));//Just for test init, redundant
                            pp.isLeafPlace();
                        }
                    }
                    placeMaptmp.put(nRow, pp);
                });
        return placeMaptmp;
    }

    private HashMap<Integer, Transition> createTransition(int[][] networkStructure, HashMap<Integer, Place> placeMaptmp){
        HashMap<Integer, Transition> transitionMaptmp = new HashMap();
        //Set transition,index start from 0
        IntStream.range(0, networkStructure.length)
                .forEach(nRow -> IntStream.range(0, networkStructure[nRow].length)
                        .filter(nCol -> networkStructure[nRow][nCol] >= 0)
                        .forEach(nCol -> {
                            int transitionInd =  networkStructure[nRow][nCol];//Transition Index

                            if(!transitionMaptmp.containsKey(networkStructure[nRow][nCol])){
                                //For init
                                transitionMaptmp.put(transitionInd, new Transition(networkStructure[nRow][nCol], this.instances));
                            }

                            //Add input and output places between the current transition
                            transitionMaptmp.get(transitionInd).addInputPlaceMap(placeMaptmp.get(nCol));
                            transitionMaptmp.get(transitionInd).addOutputPlaceMap(placeMaptmp.get(nRow));
                        })
                );

        THRESHOLD_NUM = transitionMaptmp.values()
                .stream()
                .peek(Transition::createRelationship)
                .peek(Transition::setThresholdSize)
                .mapToInt(Transition::getThresholdSize)
                .sum();

        System.out.println("Init Prepared");
        return transitionMaptmp;
    }

    public APNNetworkStructure createNetworkStructure(){
        this.networkStructure = createNetworkStructureProcess();

        this.placeMap = createPlace(this.networkStructure);
        this.transitionMap = createTransition(this.networkStructure, this.placeMap);

        return this;
    }

    private int[][] createNetworkStructureProcess(){
        int[][] networkStructure = initNetworkStructure();
        networkStructure = new CorrelationNetwork(networkStructure, instances).template();
        return networkStructure;
    }

    public void setParameters(ArrayList<Double> thresholdList){
        int offset, start = 0, end;

        Transition curTrasition;
        for (Object o : transitionMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            curTrasition = (Transition) pair.getValue();
            offset = curTrasition.getThresholdSize();
            end = start + offset;
            curTrasition.setThresholdParameters(new ArrayList<>(thresholdList.subList(start, end)));
            start = end;
        }
    }

    private int[][] initNetworkStructure(){
        int[][] networkStructure = new int[ALLPLACENUM][FEATUREPLACENUM];
        IntStream.range(0, ALLPLACENUM)
                .forEach(row -> Arrays.fill(networkStructure[row], -1));

        return networkStructure;
    }

    /*
    * print something...
    * */
    public void printStructureValue(){
        printStructureValue(this.networkStructure);
    }

    private void printStructureValue(int[][] networkStructure){
        System.out.println();
        System.out.println("<---- APN network structure [ Input: "+FEATUREPLACENUM+", Output: "+ALLPLACENUM+" ] ---->");
        System.out.println();

        System.out.format("%6s", " ");
        for (int i=0;i<networkStructure[0].length;i++)
            System.out.format("%6s","P."+i);
        System.out.println();

        for (int i=0;i<networkStructure.length;i++){
            System.out.format("%6s","P."+i);
            for (int j=0;j<networkStructure[i].length;j++){
                System.out.format("%6d", networkStructure[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
