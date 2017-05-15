package com.model.apn.Container;

import DataStructure.Attribute;
import DataStructure.Instances;
import com.google.common.collect.Lists;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.IntStream;

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

    Iterator inputPlaceSetIterator;
    Iterator outputPlaceSetIterator;

    ArrayList<Place> totalList;
    ArrayList<Place> inputarrayList;
    ArrayList<Place> outputarrayList;
    public hashTransitionInfo(Instances instances, Transition transition){
        this.instances = instances;
        this.transition = transition;
        this.inputPlaceSet = transition.getInputPlaceSet();
        this.outputPlaceSet = transition.getOutputPlaceSet();

        this.inputPlaceSetIterator = inputPlaceSet.iterator();
        this.outputPlaceSetIterator = outputPlaceSet.iterator();


        combineSet();
        test2();
    }

    public void setHashTransitionInfo(int inputPlaceHashcode, int outputPlaceHashcode, int curTransitionHashcode){
        this.inputPlaceHashcode = inputPlaceHashcode;
        this.outputPlaceHashcode = outputPlaceHashcode;
        this.curTransitionHashcode = curTransitionHashcode;
    }

    private void test2(){
        List<List<String>> attrValueList = new ArrayList();
        for(int Ind =0;Ind<totalList.size();Ind++){
            Attribute attr = totalList.get(Ind).getAttribute();
            List<String> attrValue = instances.getMEPAMembershipMap(false).getAttributeValue(attr);
            attrValueList.add(attrValue);

            System.out.println(attr.getAttributeName());
        }

        List<List<String>> cartesianProductList = Lists.cartesianProduct(attrValueList);

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

                    System.out.println(cartesianProductList.get(cProductListInd)+" "+sum);
                });


        /*
        String[] str= new String[]{"T0", "T0", "Iris-virginica", "T0", "T0"};
        List<String> a = Arrays.asList(str);
        System.out.println(cartesianProductList.get(0).hashCode()+" "+a.hashCode());
        */
    }

    private void test(int n, StringBuilder str){

        for(int i = n;i < totalList.size();i++){

            if(i != n){
                continue;
            }

            Attribute attr = totalList.get(n).getAttribute();

            if(attr.equals(instances.getAttribute(TARGET_ATTRIBUTE))){
                System.out.print(attr.getAttributeName()+" "+outputarrayList.get(i-inputarrayList.size()+1).getRootIndex()+" ");
                continue;
            }

            ArrayList<String> attrValue = instances.getMEPAMembershipMap(false).getAttributeValue(attr);
            for(int j = 0;j<attrValue.size();j++){
                String ss = attr.getAttributeName()+"-"+attrValue.get(j)+" ";
                System.out.print(" "+n + " "+i+" "+ss+" ");
                //System.out.println(" "+n + " "+i+" "+str);
                if(i == totalList.size() - 1){
                    //System.out.println(" "+n + " "+i+" "+str);
                }
                test(n+1, str);
            }
            System.out.println();
        }

    }

    private void combineSet(){
        inputarrayList = new ArrayList(inputPlaceSet);
        outputarrayList = new ArrayList(outputPlaceSet);

        inputarrayList.addAll(outputarrayList);

        totalList = new ArrayList(inputarrayList);
    }
}
