package com.model.apn.BionicsContainer;

import java.util.ArrayList;

/**
 * Created by JACK on 2017/5/18.
 */
public class Population {
    private ArrayList<Double> parameterList;
    private ArrayList<Double> localBestParameterList;    //In one iteration
    private double fitnessValue;
    private double localBestFitness = -1;
    private ArrayList<Double> speed;

    private int aliveTime = 25;
    private int maxAliveTime = 25;

    public Population(ArrayList<Double> parameterList, double fitnessValue){
        this.parameterList = parameterList;
        this.fitnessValue = fitnessValue;
    }

    public void setAllParameter(ArrayList<Double> parameterList, double fitnessValue) {
        setParameterList(parameterList);
        setFitnessValue(fitnessValue);
    }

    public void setSpeedList(ArrayList<Double> speed){
        this.speed = speed;
    }

    public  ArrayList<Double> getSpeedList(){
        return this.speed;
    }


    public void setLocalBestParameters() {
        setLocalBestParameterList();
    }

    private void setFitnessValue(double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    private void setParameterList(ArrayList<Double> parameterList) {
        this.parameterList = parameterList;
    }

    private void setLocalBestParameterList() {
        if(fitnessValue >= localBestFitness){
            localBestParameterList = parameterList;
            localBestFitness = fitnessValue;
        }
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public ArrayList<Double> getParameterList() {
        return parameterList;
    }

    public ArrayList<Double> getLocalBestParameterList() {
        return localBestParameterList;
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
