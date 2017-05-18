package com.model.apn.BionicsContainer;

import java.util.ArrayList;

/**
 * Created by JACK on 2017/5/18.
 */
public class Population {
    ArrayList<Double> parameterList;
    ArrayList<Double> localBestParameterList;    //In one iteration
    double fitnessValue;
    double localBestAverageMSE;

    int aliveTime = 50;
    int maxAliveTime = 50;

    public Population(ArrayList<Double> parameterList, double fitnessValue){
        this.parameterList = parameterList;
        this.fitnessValue = fitnessValue;
    }
    public void setAllParameter(ArrayList<Double> parameterList, double fitnessValue) {
        setParameterList(parameterList);
        setFitnessValue(fitnessValue);
    }

    private void setFitnessValue(double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    private void setParameterList(ArrayList<Double> parameterList) {
        this.parameterList = parameterList;
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public ArrayList<Double> getParameterList() {
        return parameterList;
    }

    public boolean isAlive() {
        return aliveTime > 0;
    }

    public void decreaseAliveTime() {
        aliveTime--;
    }

    public void resetAliveTime() {
        aliveTime = maxAliveTime;
    }
}
