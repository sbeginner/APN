package com.model.apn.DataStructure;
import com.model.apn.Config;

import java.awt.*;
import java.util.*;
import java.util.stream.IntStream;

import static com.model.apn.Config.*;

/**
 * Created by jack on 2017/3/20.
 */
public class Instances {

    boolean currentMode = false;    //Train-test as true, k fold validation as false

    HashMap<Integer, Attribute> attributesMap;
    HashMap<Integer, Instance> instanceMap;
    HashMap<Integer, Instance> shuffleInstanceMap;    //Optional. shuffle the elements in instanceMap
    HashMap<Integer, Instance> trainInstanceMap;
    HashMap<Integer, Instance> testInstanceMap;

    HashMap<Integer, ArrayList<Integer>> missingValueMap;//(lineNum, attributeInd list)
    HashMap<Integer, ArrayList<Integer>> missingValueMapforTest;//(lineNum, attributeInd list)

    public Instances(){
        attributesMap = new HashMap(ATTRIBUTE_NUM);
        instanceMap = new HashMap(INSTANCE_NUM);
        trainInstanceMap = new HashMap(INSTANCE_NUM);
        testInstanceMap = new HashMap(INSTANCE_NUM);

        missingValueMap = new HashMap(ATTRIBUTE_NUM);
        missingValueMapforTest = new HashMap(ATTRIBUTE_NUM);
    }

    public void setMissingValueMap(Integer errorLine, ArrayList<Integer> missingValueAttrInd, boolean checkIsTrainTestmodeAndIsTest){
        //train-test mode: train and k-fold mode as same process, train-test mode: test might use other container to store it.
        if(checkIsTrainTestmodeAndIsTest){
            this.missingValueMapforTest.put(errorLine, missingValueAttrInd);
            return;
        }

        this.missingValueMap.put(errorLine, missingValueAttrInd);
    }

    public void setAttribute(String attributeName){
        Attribute attr = new Attribute(new StringBuilder(attributeName));
        attr.setIndex(autoResizeIndex(attributesMap));
        attributesMap.put(autoResizeIndex(attributesMap), attr);
    }

    public void setInstance(Instance inst){
        inst.setInstances(this);
        instanceMap.put(autoResizeIndex(instanceMap), inst);
    }

    public void setTrainInstance(Instance inst){
        inst.setInstances(this);
        trainInstanceMap.put(autoResizeIndex(trainInstanceMap), inst);
    }

    public void setTestInstance(Instance inst){
        inst.setInstances(this);
        testInstanceMap.put(autoResizeIndex(testInstanceMap), inst);
    }

    public void setCurrentMode(boolean currentMode){
        this.currentMode = currentMode;
    }


    public Attribute getAttribute(int index){
        return this.attributesMap.get(index);
    }

    public Instance getInstance(int index){
        return this.instanceMap.get(index);
    }

    public Instance getTrainInstance(int index){
        return this.trainInstanceMap.get(index);
    }

    public Instance getTestInstance(int index){
        return this.testInstanceMap.get(index);
    }

    public  HashMap<Integer, Attribute> getAttributeMap(){
        return this.attributesMap;
    }

    public  HashMap<Integer, Instance> getInstanceMap(){
        return this.instanceMap;
    }

    public HashMap<Integer, Instance> getTrainInstanceMap(){
        return this.trainInstanceMap;
    }

    public HashMap<Integer, Instance> getTestInstanceMap(){
        return this.testInstanceMap;
    }

    public HashMap<Integer, ArrayList<Integer>> getmissingValueMap( boolean checkIsTrainTestmodeAndIsTest){
        //System.out.println("AA");
        if(checkIsTrainTestmodeAndIsTest){
            //System.out.println(missingValueMapforTest);
            return this.missingValueMapforTest;
        }
        //System.out.println(missingValueMap);
        return this.missingValueMap;
    }

    public boolean getCurrentMode(){
        return this.currentMode;
    }


    private int autoResizeIndex(HashMap targetmap){
        return targetmap.size();
    }










    private void splitTrainTestInEachFold(int valid){
        //Only for k-fold validation, this function splits train and test data for classification
        HashMap<Integer, Instance> currentInstanceMap;
        this.trainInstanceMap.clear();
        this.testInstanceMap.clear();

        if(!INSTANCEORDER_SHUFFLE_BTN){
            currentInstanceMap = instanceMap;
        }else {
            currentInstanceMap = shuffleInstanceMap;
        }

        int front = splitMethodInWeka(valid)[0];
        int back = splitMethodInWeka(valid)[1];

        //train
        currentInstanceMap.entrySet().stream().filter(item -> !(item.getKey() >= front && item.getKey() < back))
                .forEach(item -> {
                    this.trainInstanceMap.put(autoResizeIndex(trainInstanceMap), item.getValue());
                });

        //test
        currentInstanceMap.entrySet().stream().filter(item -> item.getKey() >= front && item.getKey() < back)
                .forEach(item -> {
                    this.testInstanceMap.put(autoResizeIndex(testInstanceMap), item.getValue());
                });
    }

    private int[] splitMethodInWeka(int valid){

        int numInstForFold = INSTANCE_NUM / MAX_FOLDNUM;
        int offset, front, back;

        if(valid < INSTANCE_NUM % MAX_FOLDNUM){
            numInstForFold++;
            offset = valid;
        }else {
            offset = INSTANCE_NUM % MAX_FOLDNUM;
        }

        front = valid * numInstForFold + offset;
        back = front + numInstForFold;

        return new int[]{front, back};
    }

    public void autoCVInKFold(int valid){
        splitTrainTestInEachFold(valid);
    }

    public void autoShuffleInstanceOrder(){
        //Only for k-fold validation, this function shuffles the original instance item order by random (random seed is set).
        if(!INSTANCEORDER_SHUFFLE_BTN){
            return;
        }

        shuffleInstanceMap = new HashMap(INSTANCE_NUM);
        ArrayList<Integer> shufflekeylist = new ArrayList(instanceMap.keySet());
        Collections.shuffle(shufflekeylist, new Random(RANDOM_SEED));
        shufflekeylist.stream().forEach(orderedkey -> shuffleInstanceMap.put(orderedkey, instanceMap.get(orderedkey)));
    }

    public void setMaxFoldNum(int maxfoldnum){
        MAX_FOLDNUM = maxfoldnum;
    }

    public void setRandSeed(int randseed){
        RANDOM_SEED = randseed;
    }
}
