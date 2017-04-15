package com.model.apn.Container;

import com.model.apn.DataStructure.Attribute;
import com.model.apn.DataStructure.Instance;
import com.model.apn.Math.Arithmetic;
import com.sun.istack.internal.Nullable;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.model.apn.Config.MIN_DOUBLENUM;

/**
 * Created by JACK on 2017/4/16.
 */
public class PriorProbabilityAttr {
    ArrayList<String> targetValueList;
    ArrayList<String> attributeValueList;
    HashMap<String, ArrayList<Double>> attributeValueMap;//attributeValue, probability for each target

    public PriorProbabilityAttr(){
        attributeValueMap = new HashMap();
        attributeValueList = new ArrayList();
    }

    public void setTargetValue(ArrayList<String> targetValueList){
        this.targetValueList = targetValueList;
    }

    private void setAttributeValue(String attributeValue){
        attributeValueList.add(attributeValue);
    }

    public void set(String attributeValue, Map<String, Long> attrValue2TargetFrequency){
        ArrayList<Double> probabilityList = new  ArrayList(targetValueList.size());
        setAttributeValue(attributeValue);

        long sum = sumCalc(attrValue2TargetFrequency);

        targetValueList.stream().forEach(item -> {
            double each2TargetFrequency = Optional.ofNullable(attrValue2TargetFrequency.get(item.toString())).orElse((long) 0);
            double each2TargetProbability = Arithmetic.div(each2TargetFrequency, sum);
            if(each2TargetProbability == 0){
                each2TargetProbability = MIN_DOUBLENUM;
            }

            probabilityList.add(each2TargetProbability);
        });

        attributeValueMap.put(attributeValue, probabilityList);
        System.out.println(attributeValueMap);
    }

    private long sumCalc(Map<String, Long> attrValue2TargetFrequency){
        return new ArrayList<>(attrValue2TargetFrequency.values()).stream()
                .reduce(Long::sum)
                .orElse((long) 0);
    }

    public double getProbabilityByAttributeValue(String attributeValue, String targetValue){
        if(attributeValueMap.containsKey(attributeValue) && targetValueList.contains(targetValue)){
            //Attribute value and target value is exist in Train data
            return attributeValueMap.get(attributeValue).get(targetValueList.indexOf(targetValue));
        }
        //Attribute value and target value is not exist in Train data
        return MIN_DOUBLENUM;
    }

    public double getProbabilityByAttributeValue(int attributeValueInd, int targetValueInd){
        String attributeValue = attributeValueList.get(attributeValueInd);
        String targetValue = targetValueList.get(targetValueInd);

        //System.out.println(attributeValue+", "+targetValue+" "+getProbabilityByAttributeValue(attributeValue, targetValue));
        return getProbabilityByAttributeValue(attributeValue, targetValue);
    }

}
