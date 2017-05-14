package com.model.apn.NetworkStructure;

import DataStructure.Instances;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;

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

    public HashMap<Integer, Transition> getTransitionMap(){
        return transitionMap;
    }

    public HashMap<Integer, Place> getPlaceMap(){
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

        THRESHOLD_NUM = transitionMaptmp.entrySet()
                .stream()
                .mapToInt(obj -> obj.getValue().setThresholdSize())
                .sum();

        return transitionMaptmp;
    }

    public void createNetworkStructure(){
        this.networkStructure = netStructureExample();

        this.placeMap = createPlace(this.networkStructure);
        this.transitionMap = createTransition(this.networkStructure, this.placeMap);

        this.transitionMap.values()
                .stream()
                .forEach(Transition::setSupConf);
    }

    public void setParameters(ArrayList<Double> thresholdList){
        int offset, start = 0, end;

        Transition curTrasition;
        Iterator itTrasition = transitionMap.entrySet().iterator();
        while (itTrasition.hasNext()) {
            Map.Entry pair = (Map.Entry)itTrasition.next();
            curTrasition = (Transition)pair.getValue();
            offset = curTrasition.getThresholdSize();
            end = start + offset;
            curTrasition.setParameters(new ArrayList(thresholdList.subList(start, end)));
            start = end;
        }
    }

    private int[][] netStructureExample(){
        int[][] networkStructure = new int[ALLPLACENUM][FEATUREPLACENUM];

        IntStream.range(0, ALLPLACENUM)
                .forEach(row -> Arrays.fill(networkStructure[row], -1));

        networkStructure[0][1] = 1000;
        networkStructure[2][3] = 10;
        networkStructure[2][0] = 0;
        networkStructure[2][1] = 0;
        networkStructure[3][0] = 4;
        networkStructure[3][1] = 1;
        networkStructure[4][2] = 2;
        networkStructure[5][2] = 2;
        networkStructure[6][2] = 2;
        networkStructure[4][3] = 3;
        networkStructure[5][3] = 3;
        networkStructure[6][3] = 3;

        return networkStructure;
    }

    public void printStructureValue(){
        printStructureValue(this.networkStructure);
    }

    public void printStructureValue(int[][] networkStructure){
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
