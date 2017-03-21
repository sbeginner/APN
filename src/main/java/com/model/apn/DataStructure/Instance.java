package com.model.apn.DataStructure;
import com.model.apn.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

/**
 * Created by jack on 2017/3/20.
 */
public class Instance {
    HashMap item;
    Instances instances;

    public Instance(ArrayList<String> instanceItem, Instances instances){
        item = new HashMap(Config.ATTRIBUTE_NUM);
        setInstances(instances);

        IntStream.range(0, Config.ATTRIBUTE_NUM)
                .forEach(currentAttributeInd -> {
                    instances.getAttribute(currentAttributeInd).setHashSetValue(instanceItem.get(currentAttributeInd));
                    item.put(instances.getAttribute(currentAttributeInd), instanceItem.get(currentAttributeInd));
                });


        instances.setInstance(this);
    }

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    public StringBuilder getInstanceValue(int attrIndex){
        return new StringBuilder((String) item.get(instances.getAttribute(attrIndex)));
    }

    public StringBuilder getInstanceValue(Attribute attr){
        return new StringBuilder((String)item.get(attr));
    }

    public HashMap getInstanceMap(){
        return item;
    }

}
