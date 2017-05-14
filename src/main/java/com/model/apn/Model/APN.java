package com.model.apn.Model;

import Container.MEPAMembershipMap;
import DataStructure.Instances;
import com.model.apn.Container.APNOutputInfo;
import com.model.apn.NetworkStructure.APNNetwork;
import com.model.apn.NetworkStructure.APNNetworkStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static MathCalculate.Arithmetic.div;
import static Setup.Config.INSTANCE_NUM;
import static Setup.Config.MAX_FOLDNUM;
import static Setup.Config.RANDOM_SEED;
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
        this.APNNetStructure.createNetworkStructure();
        this.APNNet = new APNNetwork(this.APNNetStructure, this.instances);
        this.APNNet.setInstances(instances);

        APNNetStructure.printStructureValue();
    }

    public void setAPNNetworkStructureParameters(){
        ArrayList<Double> list = new ArrayList(Collections.nCopies(THRESHOLD_NUM, 0.1));
        APNNet.setParameters(list);
    }

    private void setBionicsAPNNetworkStructureParameters(int initRandomSeed, int randomSeed){
        initRandomSeed += randomSeed;

        System.out.println(initRandomSeed);
        Random rnd = new Random(initRandomSeed);
        System.out.println(randomFunc(rnd));
        System.out.println(randomFunc(rnd));
        System.out.println(randomFunc(rnd));
        System.out.println(randomFunc(rnd));
        System.out.println(randomFunc(rnd));
        System.out.println(randomFunc(rnd));

        ArrayList<Double> thresholdList = new ArrayList(Collections.nCopies(THRESHOLD_NUM, 0.1));
        //System.out.println(thresholdList);
        APNNet.setParameters(thresholdList);
    }

    private double randomFunc(Random rnd){
        return div(rnd.nextInt(10000), 10000);
    }

    public void setBionicsAPNnetworkStructure(int curfoldInd){
        int initRandomSeed = (int)(RANDOM_SEED * MAX_FOLDNUM * INSTANCE_NUM * (0.87))+curfoldInd;

        setBionicsAPNNetworkStructureParameters(initRandomSeed, 0);
        travelBionicsAPNmodel();
    }

    public void travelBionicsAPNmodel(){
        APNNet.bioTravel();
    }

    public void travelAPNmodel(){
        APNNet.travel();
    }

    public void getEachOutput(){
        APNNet.getEachConfusionMatrixOutput();
    }

    public void getTotalOutput(){
        APNNet.getTotalConfusionMatrixOutput();
    }

}
