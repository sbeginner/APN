package com.model.apn.DataStructure;
import com.model.apn.Config;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jack on 2017/3/20.
 */
public class Instances {
    HashMap<Integer, Attribute> attributesMap;
    HashMap<Integer, Instance> instanceMap;
    HashMap<Integer, Instance> trainInstanceMap;
    HashMap<Integer, Instance> testInstanceMap;

    public Instances(){
        attributesMap = new HashMap(Config.ATTRIBUTE_NUM);
        instanceMap = new HashMap(Config.INSTANCE_NUM);
        trainInstanceMap = new HashMap(Config.INSTANCE_NUM);
        testInstanceMap = new HashMap(Config.INSTANCE_NUM);
    }

    public void setAttribute(String attributeName){
        Attribute attr = new Attribute(new StringBuilder(attributeName));
        attr.setIndex(autoResizeIndex(attributesMap));
        attributesMap.put(autoResizeIndex(attributesMap), attr);
        //System.out.println(attributeslist.get(attributeslist.size()-1).attributeName);
    }

    public void setInstance(Instance inst){
        inst.setInstances(this);
        instanceMap.put(autoResizeIndex(instanceMap), inst);
        //System.out.println(autoResizeIndex(instancelist)+"AA");
    }

    public void setTrainInstance(Instance inst){
        inst.setInstances(this);
        trainInstanceMap.put(autoResizeIndex(trainInstanceMap), inst);
        //System.out.println(autoResizeIndex(instancelist)+"AA");
    }

    public void setTestInstance(Instance inst){
        inst.setInstances(this);
        testInstanceMap.put(autoResizeIndex(testInstanceMap), inst);
        //System.out.println(autoResizeIndex(instancelist)+"AA");
    }

    public Attribute getAttribute(int index){
        return attributesMap.get(index);
    }

    public Instance getInstance(int index){
        return instanceMap.get(index);
    }

    public Instance getTrainInstance(int index){
        return trainInstanceMap.get(index);
    }

    public Instance getTestInstance(int index){
        return testInstanceMap.get(index);
    }

    public  HashMap<Integer, Attribute> getAttributeMap(){
        return attributesMap;
    }

    public  HashMap<Integer, Instance> getInstanceMap(){
        return instanceMap;
    }

    public HashMap<Integer, Instance> getTrainInstanceMap(){
        return trainInstanceMap;
    }

    public HashMap<Integer, Instance> getTestInstanceMap(){
        return testInstanceMap;
    }

    private int autoResizeIndex(HashMap targetmap){
        return targetmap.size();
    }
}
