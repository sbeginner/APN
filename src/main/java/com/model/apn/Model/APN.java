package com.model.apn.Model;

import Container.MEPAMembershipMap;
import DataStructure.Instances;
import com.model.apn.Container.APNOutputInfo;
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
    APNOutputInfo vAPNOutputInfo;

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
        APNNet = new APNNetwork(this.APNNetStructure, this.instances);
    }

    public void setAPNNetworkStructureParameters(){

        APNNet.setInstances(instances);
        ArrayList<Double> list = new ArrayList(Collections.nCopies(THRESHOLD_NUM, 0.1));
        list.set(2,0.9);
        list.set(5,0.9);
        list.set(7,0.9);
        APNNet.setParameters(list);

    }

    public void travelAPNmodel(){
        APNNet.travel();
    }

    public void getOutput(){
        APNNet.getOutput();
    }

    public void test(){

    }

}
