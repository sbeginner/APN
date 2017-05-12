package com.model.apn.Model;

import Container.MEPAMembershipMap;
import DataStructure.Instances;
import com.model.apn.NetworkStructure.APNNetwork;
import com.model.apn.NetworkStructure.APNNetworkStructure;

import java.util.ArrayList;
import java.util.Collections;

import static com.model.apn.Setup.Config.ATTRIBUTE_NUM;
import static com.model.apn.Setup.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Setup.Config.THRESHOLD_NUM;

/**
 * Created by jack on 2017/3/29.
 */
public class APN {
    private Instances instances;
    private APNNetwork APNNet;
    private APNNetworkStructure APNNetStructure;
    private boolean isAPNNetSet = false;

    public APN(){

    }

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    public void setAPNNetworkStructure(boolean isFixed){
        if(this.isAPNNetSet){
            return;
        }

        isAPNNetSet = isFixed;
        this.APNNetStructure = new APNNetworkStructure(instances);
        APNNetStructure.createNetworkStructure();
    }

    public void setAPNNetworkStructureParameters(){
        APNNetwork nettmp = new APNNetwork(this.APNNetStructure, this.instances);

        ArrayList<Double> list = new ArrayList(Collections.nCopies(THRESHOLD_NUM, 0.1));
        list.set(2,0.9);
        list.set(5,0.9);
        list.set(7,0.9);
        nettmp.setParameters(list);

        APNNet = nettmp;
    }

    public void travelAPNmodel(){
        APNNet.travel();
    }

    public void getOutput(){

    }

    public void test(){

    }

    private void test1(){


        /*
        MEPAMembershipMap trainMEPAMembershipMap = instances.getMEPAMembershipMap(false);
        for(int i=0;i<ATTRIBUTE_NUM;i++){
            System.out.println(instances.getAttribute(i).getThresholdList()+" "+instances.getAttribute(i).getIndex()+" "+instances.getAttributeMap().size());
            for(int j = 0;j<5;j++){
                MEPAMembership mtmp = trainMEPAMembershipMap.getAllInstanceByAttr(i).get(j);
                System.out.print(instances.getTrainInstance(j).getInstanceValue(i)+" "+mtmp.getMembership()+" "+mtmp.getMembershipDegree());
                System.out.println();
            }
        }
        */



    }

}
