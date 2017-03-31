package com.model.apn.Preprocess;

import com.model.apn.Container.MEPAConcernAttr;
import com.model.apn.Container.MEPAConcernAttrList;
import com.model.apn.DataStructure.Instances;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.model.apn.Config.TARGET_ATTRIBUTE;

/**
 * Created by jack on 2017/3/30.
 */
public class MEPA extends MEPAEntropy{
    private Instances instances;
    private Instances processInstances;
    private int divConstraintNum = 0;
    private int divConstraintNumCnt = 0;
    private Queue<ArrayList<MEPAConcernAttr>> queueForChildsInstance;
    private ArrayList<Double> bestThresholdList;

    public MEPA(){
        this.queueForChildsInstance = new LinkedList();
        this.bestThresholdList = new ArrayList<>();
    }

    public void useFilter(){
        setDivConstraintNum(2);
        ArrayList<MEPAConcernAttr> transTrainInstancList = calcTargetValueNumInEachAttribute(0);
        bestThresholdList = bestThresholdList.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(bestThresholdList);

        transTrainInstancList.stream().forEach(item -> {
            double curNum = item.getConcernAttribute();

            if(bestThresholdList.get(0) >= curNum){

                System.out.println("[ "+bestThresholdList.get(0)+", -inf]");
            }
            IntStream.range(0, bestThresholdList.size() - 1).forEach(i -> {
                if(bestThresholdList.get(i+1) >= curNum && curNum > bestThresholdList.get(i)){
                    System.out.println("[ "+bestThresholdList.get(i+1)+", "+bestThresholdList.get(i)+"]");
                }
            });
            if(curNum > bestThresholdList.get(bestThresholdList.size() - 1)){
                System.out.println("[ +inf,"+bestThresholdList.get(bestThresholdList.size() - 1)+"]");
            }
        });
    }

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    public void setDivConstraintNum(int divConstraintNum){
        this.divConstraintNum = divConstraintNum;
    }

    private ArrayList<MEPAConcernAttr> calcTargetValueNumInEachAttribute(int attributeInd){
        //init
        initDivConstraintNumCnt();
        initQueueForChildsInstance();
        initBestThresholdList();

        //Set attribute value list, and select the list item to travel the whole instances,
        //this work aims to find out the best split threshold (by using the concept of entropy).
        ArrayList<Double> curAttrValueSorted = getInputInstances().getAttribute(attributeInd).getAllValueInDigital();

        //transfer the whole train instances to only two columns(attributes), including considered column and target.
        ArrayList<MEPAConcernAttr> transTrainInstancList = transTrainInstance(attributeInd);

        //Recursive call method by queue process, finding out the best split threshold list by evaluating the Information Gain
        //Each splitting work can produce two child, and the next splitting step can produce the other two child based on there father,
        //therefore, recursive call method used
        queueForChildsInstance.offer(transTrainInstancList);
        calcEntropy(queueForChildsInstance.poll(), curAttrValueSorted);

        return transTrainInstancList;
    }



    private ArrayList<MEPAConcernAttr> transTrainInstance(int attributeInd){

        MEPAConcernAttrList concernAttrList = new MEPAConcernAttrList();

        getInputInstances().getTrainInstanceMap().entrySet().stream().forEach(instance -> {
            concernAttrList.addMEPAConcernAttr(instance.getValue().getInstanceDigitalValue(attributeInd), instance.getValue().getInstanceValue(TARGET_ATTRIBUTE));
        });

        return concernAttrList.getConcernAttrList();
    }

    private void calcEntropy(ArrayList<MEPAConcernAttr> concernAttrArrayList, ArrayList<Double> attrValueSorted){

        double bestThreshold = findBestThreshold(groupByTargetLevelFunc(concernAttrArrayList), groupByLevelFunc(concernAttrArrayList), attrValueSorted);
        bestThresholdList.add(bestThreshold);
        autoDivConstraintNumCnt();

        splitChildsInstance(concernAttrArrayList, bestThreshold);

        if(checkDivConstraintNumCnt() > divConstraintNum || queueForChildsInstance.isEmpty()){
            return;
        }

        calcEntropy(queueForChildsInstance.poll(), attrValueSorted);
    }

    private void splitChildsInstance(ArrayList<MEPAConcernAttr> concernAttrArrayList, double bestThreshold){
        queueChildsOffer(upperSplitChildsInstance(concernAttrArrayList, bestThreshold),
                lowerSplitChildsInstance(concernAttrArrayList, bestThreshold));
    }

    private void queueChildsOffer(ArrayList<MEPAConcernAttr> upperChildList, ArrayList<MEPAConcernAttr> lowerChildList){
        if(checkChildListExist(upperChildList, lowerChildList)){
            queueForChildsInstance.offer(upperChildList);
            queueForChildsInstance.offer(lowerChildList);
        }
    }


    private int checkDivConstraintNumCnt(){
        return divConstraintNumCnt;
    }

    private int autoDivConstraintNumCnt(){
        return divConstraintNumCnt++;
    }

    private void initDivConstraintNumCnt(){
        this.divConstraintNumCnt = 0;
    }

    private void initQueueForChildsInstance(){
        this.queueForChildsInstance.clear();
    }

    private void initBestThresholdList(){
        this.bestThresholdList.clear();
    }

    private Instances getInputInstances(){
        return instances;
    }



    public Instances getInstances(){
        return processInstances;
    }
}
