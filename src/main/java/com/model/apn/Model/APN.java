package com.model.apn.Model;

import DataStructure.Instances;
import com.model.apn.BionicsContainer.Population;
import com.model.apn.BionicsMethod.ABC;
import com.model.apn.BionicsMethod.Bionics;
import com.model.apn.NetworkStructure.APNNetwork;
import com.model.apn.NetworkStructure.APNNetworkStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.div;
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
        ArrayList<Double> list = new ArrayList<>(Collections.nCopies(THRESHOLD_NUM, 0.01));
        APNNet.setParameters(list);
    }

    public void setAPNNetworkStructureParameters(ArrayList<Double> list){
        APNNet.setParameters(list);
    }

    public void setBionicsParameters(ArrayList<Double> thresholdList){
        APNNet.setParameters(thresholdList);
    }

    public ArrayList<Double> setBionicsParameters(int initRandomSeed, int randomSeed){
        return setBionicsAPNNetworkStructureParameters(initRandomSeed, randomSeed);
    }

    private ArrayList<Double> setBionicsAPNNetworkStructureParameters(int initRandomSeed, int randomSeed){
        initRandomSeed += randomSeed % Double.MAX_VALUE;
        Random rnd = new Random(initRandomSeed);

        ArrayList<Double> thresholdList = new ArrayList<>(THRESHOLD_NUM);
        IntStream.range(0, THRESHOLD_NUM).forEach(thresholdInd -> thresholdList.add(randomFunc(rnd)));

        APNNet.setParameters(thresholdList);

        return thresholdList;
    }

    private double randomFunc(Random rnd){
        double randomParameter = div(rnd.nextInt(10000), 10000);
        return randomParameter == 0 ? 0.0001 : randomParameter;
    }

    public void setBionicsAPNnetworkStructure(int curfoldInd, Bionics bionics){
        int iterative = bionics.getIterative();
        Population bestPopulation = null;
        ArrayList<Population> employBeeList = new ArrayList<>();

        //Iterative
        int iterativeInd=0;
        while (iterativeInd < iterative) {

            //bionics algorithm do something
            employBeeList = bionics.bionicsMethod(this, (iterativeInd - curfoldInd + 1), employBeeList);
            Population curBestPopulation = bionics.getCurrentGlobalBestParameters(employBeeList);

            if (Objects.isNull(bestPopulation) || curBestPopulation.getFitnessValue() > bestPopulation.getFitnessValue()) {
                bestPopulation = curBestPopulation;
            }

            System.out.println(curfoldInd+" "+iterativeInd+" "+bestPopulation.getFitnessValue()+" "+bestPopulation.getParameterList());

            iterativeInd++;
        }

        System.out.println(bestPopulation.getParameterList()+" "+bestPopulation.getFitnessValue());
        setAPNNetworkStructureParameters(bestPopulation.getParameterList());
    }

    public double travelBionicsAPNmodel(){
       return APNNet.getTotalAverageMSE();
    }

    public void travelAPNmodel(int curfoldInd){
        APNNet.travel(curfoldInd);
    }

    public void getEachOutput(){
        APNNet.getEachConfusionMatrixOutput();
    }

    public void getTotalOutput(){
        APNNet.getTotalConfusionMatrixOutput();
    }

}
