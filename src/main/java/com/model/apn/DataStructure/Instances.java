package com.model.apn.DataStructure;
import com.model.apn.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.model.apn.Config.ATTRIBUTE_NUM;
import static com.model.apn.Config.INSTANCE_NUM;

/**
 * Created by jack on 2017/3/20.
 */
public class Instances {

    boolean currentMode = false;    //Train-test as true, k fold validation as false

    HashMap<Integer, Attribute> attributesMap;
    HashMap<Integer, Instance> instanceMap;
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
}
