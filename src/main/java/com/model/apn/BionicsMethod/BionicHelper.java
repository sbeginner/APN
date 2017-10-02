package com.model.apn.BionicsMethod;

import com.model.apn.BionicsContainer.Population;
import com.model.apn.Model.APN;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.div;

public class BionicHelper {
    private APN apn;
    int iterative;
    int totalPopulation;
    boolean accuracyMode = false;

    public void setAPN(APN apn){
        this.apn = apn;
    }

    public Population getCurrentGlobalBestParameters(ArrayList<Population> PopulationList) {
        return PopulationList.stream()
                .max(Comparator.comparing(Population::getFitnessValue))
                .orElse(setPopulation(1).get(0));
    }

    ArrayList<Population> setPopulation(int population_num){
        ArrayList<Population> populationList = new ArrayList<>();

        IntStream.range(0, population_num).forEach(employBeeInd -> {
            ArrayList<Double> parameters = this.apn.setBionicsParameters();
            double averageMSE = this.apn.travelBionicsAPNmodel();
            double abcFitness = fitnessFunc(averageMSE);

            populationList.add(new Population(parameters, abcFitness));
        });

        return populationList;
    }

    private double fitnessFunc(double fitnessValue)  {
        return div(1, fitnessValue + 1);
    }

    double updateFormula(double old_value, double fitness){
        return (0.9)*old_value + fitness;
    }

    ArrayList<Double> getRandomParameters(){
        return this.apn.setBionicsParameters();
    }

    double getFitness(ArrayList<Double> new_param){
        this.apn.setBionicsParameters(new_param);
        double averageMSE = this.apn.travelBionicsAPNmodel();
        return  fitnessFunc(averageMSE);
    }

    void checkIsAlive(double fitness, Population population, ArrayList<Double> newParameterList){
        if(fitness > population.getFitnessValue()){
            population.setAllParameter(newParameterList, fitness);
            population.resetAliveTime();
        }else {
            population.decreaseAliveTime();
        }
    }

    public int getIterative() {
        return iterative;
    }
}
