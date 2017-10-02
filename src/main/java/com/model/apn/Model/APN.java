package com.model.apn.Model;

import DataStructure.Instances;
import com.model.apn.BionicsContainer.Population;
import com.model.apn.BionicsMethod.ABC;
import com.model.apn.BionicsMethod.Bionics;
import com.model.apn.Container.APNOutputInfo;
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
    private boolean isAPNNetSet = false;
    private APNNetworkStructure APNNetStructure;

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    /*
    * Original APN process
    * */
    public void setAPNNetworkStructure(boolean isFixed){
        if(this.isAPNNetSet){
            return;
        }

        isAPNNetSet = isFixed;
        APNNetworkStructure APNNetStructure = new APNNetworkStructure(instances).createNetworkStructure();
        this.APNNetStructure = APNNetStructure;

        this.APNNet = new APNNetwork(this.instances);
        this.APNNet.setAPNNetStruct(APNNetStructure);
    }

    public void setAPNNetworkStructureParameters(){
        APNNet.setParameters(new ArrayList<>(Collections.nCopies(THRESHOLD_NUM, 0.01)));
    }

    public void travelAPNmodel(int curfoldInd){
        APNNet.travel(curfoldInd);
    }

    /*
    * Extra-Bionics process
    * */
    private void setAPNNetworkStructureParameters(ArrayList<Double> list){
        APNNet.setParameters(list);
    }

    public void setBionicsAPNnetworkStructure(int curfoldInd, Bionics bionics){
        int iterative = bionics.getIterative();
        Population bestPopulation = null;
        ArrayList<Population> PopulationList = new ArrayList<>();

        //Iterative
        int iterativeInd=0;
        while (iterativeInd < iterative) {

            //bionics algorithm do something
            PopulationList = bionics.bionicsMethod(this, (iterativeInd - curfoldInd + 1), PopulationList);
            Population curBestPopulation = bionics.getCurrentGlobalBestParameters(PopulationList);

            if (Objects.isNull(bestPopulation) || curBestPopulation.getFitnessValue() > bestPopulation.getFitnessValue()) {
                bestPopulation = curBestPopulation;
            }

            System.out.println(curfoldInd+" "+iterativeInd+" "+bestPopulation.getFitnessValue()+" "+bestPopulation.getParameterList());

            iterativeInd++;
        }

        setAPNNetworkStructureParameters(bestPopulation.getParameterList());
    }

    public ArrayList<Double> setBionicsParameters(){
        ArrayList<Double> thresholdList = new ArrayList<>(THRESHOLD_NUM);

        IntStream.range(0, THRESHOLD_NUM)
                .forEach(thresholdInd -> thresholdList.add(randomFunc()));

        APNNet.setParameters(thresholdList);

        return thresholdList;
    }

    public void setBionicsParameters(ArrayList<Double> thresholdList){
        APNNet.setParameters(thresholdList);
    }

    public double travelBionicsAPNmodel(){
       return APNNet.getTotalAverageMSE();
    }

    private double randomFunc(){
        Random rnd = new Random();
        double randomParameter = div(rnd.nextInt(1000), 1000);
        return randomParameter == 0 ? 0.0001 : randomParameter;
    }

    /*
    * Get output info
    * */
    public APNOutputInfo getAPNOutputInfo(){
        return APNNet.getAPNOutputInfo();
    }

    public void printNetworkStructure(){
        APNNetStructure.printStructureValue();
    }
}
