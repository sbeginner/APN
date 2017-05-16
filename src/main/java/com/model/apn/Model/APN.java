package com.model.apn.Model;

import Container.MEPAMembershipMap;
import DataStructure.Instances;
import com.model.apn.Container.APNOutputInfo;
import com.model.apn.NetworkStructure.APNNetwork;
import com.model.apn.NetworkStructure.APNNetworkStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.div;
import static Setup.Config.INSTANCE_NUM;
import static Setup.Config.MAX_FOLDNUM;
import static Setup.Config.RANDOM_SEED;
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
        ArrayList<Double> list = new ArrayList(Collections.nCopies(THRESHOLD_NUM, 0.01));
        APNNet.setParameters(list);
    }

    public void setAPNNetworkStructureParameters(ArrayList<Double> list){
        APNNet.setParameters(list);
    }

    private ArrayList<Double> setBionicsAPNNetworkStructureParameters(int initRandomSeed, int randomSeed){
        initRandomSeed += randomSeed;

        Random rnd = new Random(initRandomSeed);

        ArrayList<Double> thresholdList = new ArrayList(THRESHOLD_NUM);
        IntStream.range(0, THRESHOLD_NUM).forEach(thresholdInd -> thresholdList.add(randomFunc(rnd)));

        Collections.shuffle(thresholdList, rnd);

        APNNet.setParameters(thresholdList);

        return thresholdList;
    }

    private double randomFunc(Random rnd){
        return div(rnd.nextInt(10000), 10000);
    }

    public void setBionicsAPNnetworkStructure(int curfoldInd, Bionics bionics){
        int initRandomSeed = (int)(RANDOM_SEED * MAX_FOLDNUM * INSTANCE_NUM * (0.87)) & (curfoldInd + 1);

        double min = 9999;
        ArrayList<Double> arrayList = new ArrayList();
        for(int i=0;i<1000;i++){
            ArrayList<Double> arrayListtmp = setBionicsAPNNetworkStructureParameters(initRandomSeed, i);

            System.out.println(i);

            double averageMSE = travelBionicsAPNmodel();
            if(min > averageMSE){
                min = averageMSE;
                arrayList = arrayListtmp;
            }
        }

        setAPNNetworkStructureParameters(arrayList);
    }

    public double travelBionicsAPNmodel(){
       return APNNet.getTotalAverageMSE();
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
