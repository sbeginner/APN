package com.model.apn.DataStructure;

import com.model.apn.Math.Arithmetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import static com.model.apn.Config.ATTRIBUTE_NUM;

/**
 * Created by jack on 2017/3/20.
 */
public class Instance {
    HashMap<Attribute, String> item;
    Instances instances;

    public Instance(ArrayList<String> instanceItem, Instances instances){
        item = new HashMap(ATTRIBUTE_NUM);
        setInstances(instances);
        setItemValueAndAttrValue(instanceItem);
        instances.setInstance(this);
    }

    public Instance(ArrayList<String> instanceItem, Instances instances, Boolean isTestOrTrain){
        item = new HashMap(ATTRIBUTE_NUM);
        setInstances(instances);
        setItemValueAndAttrValue(instanceItem, isTestOrTrain);
        if(isTestOrTrain == true){
            //true for train instance
            instances.setTrainInstance(this);
        }else{
            //false for test instance
            instances.setTestInstance(this);
        }
    }

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    private void setItemValueAndAttrValue(ArrayList<String> instanceItem, Boolean isTestOrTrain){

        IntStream.range(0, ATTRIBUTE_NUM)
                .forEach(currentAttributeInd -> {

                    instances.getAttribute(currentAttributeInd)
                            .setHashMapValue(instanceItem.get(currentAttributeInd), !isTestOrTrain);

                    item.put(instances.getAttribute(currentAttributeInd), instanceItem.get(currentAttributeInd));
                });

    }

    private void setItemValueAndAttrValue(ArrayList<String> instanceItem){

        IntStream.range(0, ATTRIBUTE_NUM)
                .forEach(currentAttributeInd -> {
                    instances.getAttribute(currentAttributeInd).setHashMapValue(instanceItem.get(currentAttributeInd), false);
                    item.put(instances.getAttribute(currentAttributeInd), instanceItem.get(currentAttributeInd));
                });

    }

    public void changeItemValue(ArrayList<Integer> attributeList, boolean checkIsTest){

        if(checkIsTest){
            attributeList.stream().forEach(i->{
                item.replace(instances.getAttribute(i), instances.getAttribute(i).getMissingValueTest());
            });
            return;
        }

        attributeList.stream().forEach(i->{
            item.replace(instances.getAttribute(i), instances.getAttribute(i).getMissingValue());
        });
    }

    public StringBuilder getInstanceValue(int attrIndex){
        return new StringBuilder(item.get(instances.getAttribute(attrIndex)));
    }

    public double getInstanceDigitalValue(int attrIndex){
        //Get digital type, only use in digital type
        return Arithmetic.createDouble(item.get(instances.getAttribute(attrIndex)));
    }

    public StringBuilder getInstanceValue(Attribute attr){
        return new StringBuilder((String)item.get(attr));
    }

    public HashMap getInstanceItemMap(){
        return item;
    }

}
