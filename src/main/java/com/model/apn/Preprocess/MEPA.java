package com.model.apn.Preprocess;

import com.model.apn.Container.MEPAConcernAttr;
import com.model.apn.Container.MEPAConcernAttrList;
import com.model.apn.Container.MEPAMembership;
import com.model.apn.DataStructure.Attribute;
import com.model.apn.DataStructure.Instance;
import com.model.apn.DataStructure.Instances;
import com.model.apn.Math.Arithmetic;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.model.apn.Config.ATTRIBUTE_NUM;
import static com.model.apn.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Config.DIVIDE_CONSTRAINTNUM;
import static com.model.apn.Math.Arithmetic.createDouble;

/**
 * Created by jack on 2017/3/30.
 */
public class MEPA extends MEPAEntropy{
    private Instances instances;

    public MEPA(){
        this.queueForChildInstance = new LinkedList();
        this.bestThresholdList = new ArrayList<>();
    }


    public void setInstances(Instances instances){
        this.instances = instances;
    }

    public void setDivConstraintNum(int divConstraintNum){
        //Set the number for the range to split, divide constraint number
        DIVIDE_CONSTRAINTNUM = divConstraintNum;
    }


    public void useFilter(){
        //Travel all attributes
        IntStream.range(0, ATTRIBUTE_NUM)
                .forEach(this::MEPAProcess);
    }

    private void MEPAProcess(int attributeInd){

        Attribute curAttribute = instances.getAttribute(attributeInd);

        if(curAttribute.getAttributeType() == true){
            //If attribute type is string goes here
            //Membership degree is 1.0
            remainInstanceInfo(curAttribute, true);
            remainInstanceInfo(curAttribute, false);
        }else{
            //If attribute type is digital goes here
            //Train instance uses to find threshold, and their membership degree set 1.0
            //Test instance needs to calculate the membership degree for each
            setTrainInstanceInfo(curAttribute);
            setTestInstanceInfo(curAttribute);
        }

    }

    private void remainInstanceInfo(Attribute curAttribute, boolean isTest){
        HashMap<Integer, Instance> instanceMap;

        if(isTest){
            instanceMap = instances.getTestInstanceMap();
        }else
            instanceMap = instances.getTrainInstanceMap();

        ArrayList<MEPAMembership> MEPAMembershipList = instanceMap
                .entrySet()
                .stream()
                .map(item -> new MEPAMembership(item.getValue().getInstanceValue(curAttribute).toString(), 1))
                .collect(Collectors.toCollection(ArrayList::new));

        instances.setMEPAMembershipEachFold(curAttribute, MEPAMembershipList, isTest);
    }

    private void setTrainInstanceInfo(Attribute curAttribute){
        //Find out the threshold by using Information Gain method
        //We use the training data set to find out those threshold, and use these threshold in testing data set
        calcTargetValueNumInEachAttribute(curAttribute.getIndex());

        //Sort the identified threshold, it can be more simple to group the data set (instance)
        bestThresholdListSort(curAttribute);

        ArrayList<MEPAMembership> MEPAMembershipList = instances.getTrainInstanceMap()
                .entrySet()
                .stream()
                .map(trainInstance -> {
                    //It's not necessary to calculate the membership degree in training data, therefore, just directly set the degree in 1.0
                    return new MEPAMembership(trainInstance.getValue().getInstanceValue(curAttribute).toString(), 1);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        instances.setMEPAMembershipEachFold(curAttribute, MEPAMembershipList, false);
    }

    private void calcTargetValueNumInEachAttribute(int attributeInd){
        //Init
        initTargetValueNumInEachAttribute();

        //Set attribute value list, and select the list item to travel the whole instances,
        //this work aims to find out the best split threshold (by using the concept of entropy).
        ArrayList<Double> curAttrValueList = getInputInstances().getAttribute(attributeInd).getAllTrainValueInDigital();

        //transfer the whole train instances to only two columns(attributes), including considered column and target.
        ArrayList<MEPAConcernAttr> transTrainInstanceList = transTrainInstance(attributeInd);

        //Recursive call method by queue process, finding out the best split threshold list by evaluating the Information Gain
        //Each splitting work can produce two child, and the next splitting step can produce the other two child based on there father,
        //therefore, recursive call method used
        getQueueForChildInstance().offer(transTrainInstanceList);
        calcEntropy(getQueueForChildInstance().poll(), curAttrValueList); //The most important place for calculating (Entropy...)
    }

    private void bestThresholdListSort(Attribute curAttribute){
        //Sort the best threshold set, including distinct process
        bestThresholdList = bestThresholdList.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
        //Attribute set thresholdList
        curAttribute.setThresholdList(bestThresholdList);
    }

    private ArrayList<MEPAConcernAttr> transTrainInstance(int attributeInd){
        //Only Current attribute and target attribute will be selected
        MEPAConcernAttrList concernAttrList = new MEPAConcernAttrList();

        getInputInstances().getTrainInstanceMap().entrySet().stream().forEach(instance -> {
            concernAttrList.addMEPAConcernAttr(instance.getValue().getInstanceDigitalValue(attributeInd), instance.getValue().getInstanceValue(TARGET_ATTRIBUTE));
        });

        return concernAttrList.getConcernAttrList();
    }

    private void setTestInstanceInfo(Attribute curAttribute){
        //Set the test instance by using the threshold that train data find
        ArrayList<MEPAMembership> MEPAMembershipList =  instances.getTestInstanceMap()
                .entrySet()
                .stream()
                .map(item -> detectValueRangeChangeToStr(createDouble(item.getValue().getInstanceValue(curAttribute).toString())))
                .collect(Collectors.toCollection(ArrayList::new));

        instances.setMEPAMembershipEachFold(curAttribute, MEPAMembershipList, true);
    }

    private MEPAMembership detectValueRangeChangeToStr(double curNum){
        //The number categorize into its range
        String replaceStr;
        if(bestThresholdList.get(0) >= curNum){
            replaceStr = String.valueOf("[ "+bestThresholdList.get(0)+", -inf]");
            return new MEPAMembership(replaceStr, 1);
        }

        if(curNum > bestThresholdList.get(bestThresholdList.size() - 1)){
            replaceStr = String.valueOf("[ inf, "+bestThresholdList.get(bestThresholdList.size() - 1)+"+]");
            return new MEPAMembership(replaceStr, 1);
        }

        int middleNum = IntStream.range(0, bestThresholdList.size() - 1)
                .filter(i -> bestThresholdList.get(i+1) >= curNum && curNum > bestThresholdList.get(i))
                .findFirst()
                .orElse(-99);

        double leftThreshold = bestThresholdList.get(middleNum+1);
        double rightThreshold = bestThresholdList.get(middleNum);
        replaceStr = String.valueOf("[ "+bestThresholdList.get(middleNum+1)+", "+bestThresholdList.get(middleNum)+"+]");

        return new MEPAMembership(replaceStr, membershipDegree(leftThreshold, rightThreshold, curNum));
    }

    private double membershipDegree(double leftThreshold, double rightThreshold, double currentNum){
        return Arithmetic.div(Arithmetic.sub(currentNum, rightThreshold), Arithmetic.sub(leftThreshold, rightThreshold));
    }



    private Instances getInputInstances(){
        return instances;
    }

    public Instances getInstances(){
        return instances;
    }
}
