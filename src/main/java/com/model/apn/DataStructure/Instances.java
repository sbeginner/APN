package com.model.apn.DataStructure;
import com.model.apn.Config;

import java.util.HashMap;

/**
 * Created by jack on 2017/3/20.
 */
public class Instances {
    HashMap<Integer, Attribute> attributeslist;
    HashMap<Integer, Instance> instancelist;

    public Instances(){
        attributeslist = new HashMap(Config.ATTRIBUTE_NUM);
        instancelist = new HashMap(Config.INSTANCE_NUM);
    }

    public void setAttribute(String attributeName){
        Attribute attr = new Attribute(new StringBuilder(attributeName));
        attr.setIndex(autoResizeIndex(attributeslist));
        attributeslist.put(autoResizeIndex(attributeslist), attr);
        //System.out.println(attributeslist.get(attributeslist.size()-1).attributeName);
    }

    public void setInstance(Instance inst){
        inst.setInstances(this);
        instancelist.put(autoResizeIndex(instancelist), inst);
        //System.out.println(autoResizeIndex(instancelist)+"AA");
    }

    public Attribute getAttribute(int index){
        return attributeslist.get(index);
    }

    public Instance getInstance(int index){
        return instancelist.get(index);
    }

    public  HashMap<Integer, Attribute> getAttributeList(){
        return attributeslist;
    }

    public  HashMap<Integer, Instance> getInstanceList(){
        return instancelist;
    }

    private int autoResizeIndex(HashMap targetmap){
        return targetmap.size();
    }
}
