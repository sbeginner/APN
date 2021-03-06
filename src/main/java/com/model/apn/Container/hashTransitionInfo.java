package com.model.apn.Container;

import DataStructure.Attribute;
import DataStructure.Instances;
import com.google.common.collect.Lists;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.div;
import static Setup.Config.INSTANCE_NUM_TRAIN;

/**
 * Created by JACK on 2017/5/15.
 */
public class hashTransitionInfo {
    private Instances instances;
    private HashSet<Place> inputPlaceSet;
    private HashSet<Place> outputPlaceSet;
    private HashMap<List<String>, Integer> ConfInputOutpuFreqMap;
    private HashMap<List<String>, Integer> SupInputOutpuFreqMap;

    public hashTransitionInfo(Instances instances, Transition transition){
        this.instances = instances;
        this.inputPlaceSet = transition.getInputPlaceSet();
        this.outputPlaceSet = transition.getOutputPlaceSet();
        this.ConfInputOutpuFreqMap = new HashMap<>();
        this.SupInputOutpuFreqMap = new HashMap<>();
    }

    public ArrayList<Double> setSupport(){
        List<String> arr;
        ArrayList<Place> totalList_Input = new ArrayList<>(inputPlaceSet);
        ArrayList<Double> supportList = new ArrayList<>();

        for(Place p:totalList_Input){
            arr = new ArrayList<>();
            arr.add(p.getTestAttributeValue());
            arr.addAll(this.outputPlaceSet.stream().map(Place::getTestAttributeValue).collect(Collectors.toList()));

            double eachInputFreq = getInputOutpuFreqMap(arr, false);
            double total = INSTANCE_NUM_TRAIN;

            supportList.add(div(eachInputFreq, total));
        }

        return supportList;
    }

    public double setConfidence(){
        List<String> arr = new ArrayList<>();
        this.inputPlaceSet.forEach(i-> arr.add(i.getTestAttributeValue()));
        double inputFreq = getInputOutpuFreqMap(arr, true);

        this.outputPlaceSet.forEach(i-> arr.add(i.getTestAttributeValue()));
        double inputUnionOputFreq = getInputOutpuFreqMap(arr, true);

        double result;
        result = div(inputUnionOputFreq, inputFreq);
        if(Double.isNaN(result)){
            result = 1e-10;
        }

        return result;
    }

    public void createConfidence(){
        ArrayList<Place> combineList;

        combineList = new ArrayList<>(inputPlaceSet);
        calcCProductListFrequency(combineList, setCartesianProductList(combineList), true);

        combineList = combineSet();
        calcCProductListFrequency(combineList, setCartesianProductList(combineList), true);
    }

    public void createSupport(){
        ArrayList<Place> totalList_Input = new ArrayList<>(inputPlaceSet);
        for(Place p:totalList_Input){
            ArrayList<Place> p1 = new ArrayList<>();
            p1.add(p);
            ArrayList<Place> totalList_output = new ArrayList<>(outputPlaceSet);
            p1.addAll(totalList_output);

            calcCProductListFrequency(p1, setCartesianProductList(p1), false);
        }
    }

    private List<List<String>> setCartesianProductList(ArrayList<Place> combineList){
        List<List<String>> attrValueList = new ArrayList<>();

        for (Place aCombineList : combineList) {
            Attribute attr = aCombineList.getAttribute();
            List<String> attrValue = instances.getMEPAMembershipMap(false).getAttributeValue(attr);
            attrValueList.add(attrValue);
        }

        return Lists.cartesianProduct(attrValueList);
    }

    private void calcCProductListFrequency(ArrayList<Place> totalList, List<List<String>> cartesianProductList, boolean isConf){

        IntStream.range(0, cartesianProductList.size())
                .forEach(cProductListInd->{
                    List<String> cartesianProduct = cartesianProductList.get(cProductListInd);

                    int sum = IntStream.range(0, INSTANCE_NUM_TRAIN).filter(instanceInd -> {
                        boolean isNoMatch = IntStream.range(0, cartesianProduct.size()).filter(curAttributeInd->{
                            Attribute attr = totalList.get(curAttributeInd).getAttribute();
                            String cValue = cartesianProduct.get(curAttributeInd);
                            String cInstanceValue = instances.getMEPAMembership(attr, false).get(instanceInd).getMembership();

                            return !cValue.equals(cInstanceValue);
                        }).findFirst().isPresent();

                        return !isNoMatch;
                    }).map(instanceInd -> 1).sum();

                    if(sum > 0){
                        if(isConf){
                            ConfInputOutpuFreqMap.put(cartesianProduct, sum);
                        }else {
                            SupInputOutpuFreqMap.put(cartesianProduct, sum);
                        }
                    }

                });
    }

    private double getInputOutpuFreqMap(List<String> arr, boolean isConf){
        if(isConf){
            return Optional.ofNullable(ConfInputOutpuFreqMap.get(arr)).orElse(0);
        }
        return Optional.ofNullable(SupInputOutpuFreqMap.get(arr)).orElse(0);
    }

    private ArrayList<Place> combineSet(){
        ArrayList<Place> inputarrayList = new ArrayList<>(inputPlaceSet);
        ArrayList<Place> outputarrayList = new ArrayList<>(outputPlaceSet);

        inputarrayList.addAll(outputarrayList);

        return new ArrayList<>(inputarrayList);
    }
}
