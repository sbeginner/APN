package com.model.apn.Container;

import DataStructure.Attribute;
import DataStructure.Instances;
import com.model.apn.APNObject.Place;
import com.model.apn.APNObject.Transition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;

import static Setup.Config.TARGET_ATTRIBUTE;

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

    public hashTransitionInfo(Instances instances, Transition transition){
        this.instances = instances;
        this.transition = transition;
        this.inputPlaceSet = transition.getInputPlaceSet();
        this.outputPlaceSet = transition.getOutputPlaceSet();

        this.inputPlaceSetIterator = inputPlaceSet.iterator();
        this.outputPlaceSetIterator = outputPlaceSet.iterator();


        combineSet();
        test(0);
    }

    public void setHashTransitionInfo(int inputPlaceHashcode, int outputPlaceHashcode, int curTransitionHashcode){
        this.inputPlaceHashcode = inputPlaceHashcode;
        this.outputPlaceHashcode = outputPlaceHashcode;
        this.curTransitionHashcode = curTransitionHashcode;
    }

    private void test(int n){

        if(n > totalList.size()){
            return;
        }

        for(int i=n;i<totalList.size();i++){
            Attribute attr = totalList.get(i).getAttribute();
            ArrayList<String> attrValue = instances.getMEPAMembershipMap(false).getAttributeValue(attr);

            attrValue.forEach(str->{
                System.out.print(attr.getAttributeName()+" "+str+" ");
                test(n+1);
            });
            System.out.println();
        }

    }

    private void combineSet(){
        ArrayList<Place> inputarrayList = new ArrayList(inputPlaceSet);
        ArrayList<Place> outputarrayList = new ArrayList(outputPlaceSet);

        inputarrayList.addAll(outputarrayList);

        totalList = new ArrayList(inputarrayList);
    }
}
