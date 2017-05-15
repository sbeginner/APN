package com.model.apn.Container;

import DataStructure.Attribute;
import DataStructure.Instances;
import com.google.common.collect.Lists;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;

import javax.swing.text.html.Option;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.div;
import static Setup.Config.INSTANCE_NUM_TEST;
import static Setup.Config.INSTANCE_NUM_TRAIN;
import static Setup.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Setup.Config.THRESHOLD_NUM;

/**
 * Created by JACK on 2017/5/15.
 */
public class hashTransitionInfo {
    private Instances instances;

    private Transition transition;
    private int inputPlaceHashcode;
    private int outputPlaceHashcode;
    private int curTransitionHashcode;
    private HashSet<Place> inputPlaceSet;
    private HashSet<Place> outputPlaceSet;
    private int mixHashcode;

    private HashMap<List<String>, Integer> InputOutpuFreqMap;


    public hashTransitionInfo(Instances instances, Transition transition){
        this.instances = instances;
        this.transition = transition;
        this.inputPlaceSet = transition.getInputPlaceSet();
        this.outputPlaceSet = transition.getOutputPlaceSet();
        this.InputOutpuFreqMap = new HashMap();
    }

    public ArrayList<Double> setSupport(){
        List<String> arr;
        ArrayList<Place> totalList_Input = new ArrayList<>(inputPlaceSet);
        ArrayList<Double> supportList = new ArrayList<>();


        for(Place p:totalList_Input){
            arr = new ArrayList<>();
            arr.add(p.getTestAttributeValue());
            arr.addAll(this.outputPlaceSet.stream().map(Place::getTestAttributeValue).collect(Collectors.toList()));

            double eachInputFreq = getInputOutpuFreqMap(arr);
            double total = INSTANCE_NUM_TRAIN;


            supportList.add(div(eachInputFreq, total));
        }

        return supportList;
    }

    public double setConfidence(){
        List<String> arr = new ArrayList<>();
        this.inputPlaceSet.forEach(i-> arr.add(i.getTestAttributeValue()));
        double inputFreq = getInputOutpuFreqMap(arr);
        this.outputPlaceSet.forEach(i-> arr.add(i.getTestAttributeValue()));
        double inputUnionOputFreq = getInputOutpuFreqMap(arr);

        return div(inputUnionOputFreq,inputFreq);
    }

    public void createConfidence(){
        ArrayList<Place> combineList = combineSet();
        calcCProductListFrequency(combineList, setCartesianProductList(combineList));

        combineList = new ArrayList<>(inputPlaceSet);
        calcCProductListFrequency(combineList, setCartesianProductList(combineList));
    }

    public void createSupport(){
        ArrayList<Place> totalList_Input = new ArrayList<>(inputPlaceSet);
        for(Place p:totalList_Input){
            ArrayList<Place> p1 = new ArrayList<>();
            p1.add(p);
            ArrayList<Place> totalList_output = new ArrayList<>(outputPlaceSet);
            p1.addAll(totalList_output);

            calcCProductListFrequency(p1, setCartesianProductList(p1));
        }
    }

    public void setHashTransitionInfo(int inputPlaceHashcode, int outputPlaceHashcode, int curTransitionHashcode){
        this.inputPlaceHashcode = inputPlaceHashcode;
        this.outputPlaceHashcode = outputPlaceHashcode;
        this.curTransitionHashcode = curTransitionHashcode;
    }

    private List<List<String>> setCartesianProductList(ArrayList<Place> combineList){
        List<List<String>> attrValueList = new ArrayList();

        for (Place aCombineList : combineList) {
            Attribute attr = aCombineList.getAttribute();
            List<String> attrValue = instances.getMEPAMembershipMap(false).getAttributeValue(attr);
            attrValueList.add(attrValue);
        }

        return Lists.cartesianProduct(attrValueList);
    }

    private void calcCProductListFrequency(ArrayList<Place> totalList, List<List<String>> cartesianProductList){

        IntStream.range(0, cartesianProductList.size())
                .forEach(cProductListInd->{
                    int sum = IntStream.range(0, INSTANCE_NUM_TRAIN).map(instanceInd->{

                        boolean isNoMatch = IntStream.range(0, totalList.size()).filter(curAttributeInd->{
                            Attribute attr = totalList.get(curAttributeInd).getAttribute();
                            String cValue = cartesianProductList.get(cProductListInd).get(curAttributeInd);
                            String cInstanceValue = instances.getMEPAMembership(attr, false).get(instanceInd).getMembership();

                            return !cValue.equals(cInstanceValue);
                        }).findAny().isPresent();

                        if(isNoMatch){
                            return 0;
                        }else {
                            return 1;
                        }
                    }).sum();

                    if(sum > 0){
                        InputOutpuFreqMap.put(cartesianProductList.get(cProductListInd), sum);
                    }
                });
    }

    private double getInputOutpuFreqMap(List<String> arr){
        return Optional.ofNullable(InputOutpuFreqMap.get(arr)).orElse(0);
    }

    private ArrayList<Place> combineSet(){
        ArrayList<Place> inputarrayList = new ArrayList<>(inputPlaceSet);
        ArrayList<Place> outputarrayList = new ArrayList<>(outputPlaceSet);

        inputarrayList.addAll(outputarrayList);

        return new ArrayList<>(inputarrayList);
    }
}
