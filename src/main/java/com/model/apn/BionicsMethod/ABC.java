package com.model.apn.BionicsMethod;

import com.model.apn.BionicsContainer.Population;
import com.model.apn.Model.APN;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.distribution.EnumeratedDistribution;

import java.util.*;
import java.util.stream.Collectors;
import static MathCalculate.Arithmetic.div;
import static MathCalculate.Arithmetic.mul;
import static MathCalculate.Arithmetic.round;

/**
 * Created by JACK on 2017/5/14.
 */
public class ABC extends BionicHelper implements Bionics{
    private double employBeePercent = 0.1;
    private double onlookerBeePercent = 0.4;
    private double scoutBeePercent = 0.5;

    public ABC(int iterative, int totalPopulation, boolean accuracyMode){
        //Scout bee can be calculated
        this.iterative = iterative;
        this.totalPopulation = totalPopulation;
        this.accuracyMode = accuracyMode;
    }

    public void setDifferentBeePercent(Double employBeePercent, Double onlookerBeePercent, Double scoutBeePercent){

        double sum = employBeePercent + onlookerBeePercent + scoutBeePercent;
        if(sum > 1){
            this.employBeePercent = div(employBeePercent, sum);
            this.onlookerBeePercent = div(onlookerBeePercent, sum);
            this.scoutBeePercent = div(scoutBeePercent, sum);
            return;
        }

        this.employBeePercent = employBeePercent;
        this.onlookerBeePercent = onlookerBeePercent;
        this.scoutBeePercent = scoutBeePercent;
    }

    @Override
    public ArrayList<Population> bionicsMethod(APN apn, int randseed, ArrayList<Population> PopulationList) {
        setAPN(apn);

        PopulationList =  updateParametersPolicy(PopulationList);

        return new ArrayList<>(PopulationList.stream()
                .filter(Population::isAlive)
                .collect(Collectors.toList()));
    }

    @Override
    public ArrayList<Population> updateParametersPolicy(ArrayList<Population>  PopulationList){
        int employBeeNum = (int) mul(totalPopulation, employBeePercent);
        int onlookerBeeNum = (int) mul(totalPopulation, onlookerBeePercent);

        if(PopulationList.isEmpty()){
            PopulationList = setPopulation(employBeeNum);
        }

        PopulationList = employBeeFlyingProcess(PopulationList);
        PopulationList = onlookerBeeFlyingProcess(PopulationList, onlookerBeeNum);
        PopulationList = scoutBeeFlyingProcess(PopulationList, totalPopulation, onlookerBeeNum);

        return PopulationList;
    }

    // the constraint bee
    private ArrayList<Population> employBeeFlyingProcess(ArrayList<Population> employBeeList){

        if(!accuracyMode){
            return employBeeList;
        }

        employBeeList.forEach(employBee -> {
            ArrayList<Double> old_param = employBee.getParameterList();
            ArrayList<Double> new_param = new ArrayList<>();

            List<Double> newParameterListtmp = old_param.stream()
                    .map(this::updateFormula)
                    .collect(Collectors.toList());
            new_param.addAll(newParameterListtmp);

            double fitness =  getFitness(new_param);
            checkIsAlive(fitness, employBee, new_param);
        });

        return employBeeList;
    }

    // onlooker bee help employee bee, first they'll find out follow the higher fitness value
    private ArrayList<Population> onlookerBeeFlyingProcess(ArrayList<Population> employBeeList, int onlookerBeeNum){
        HashMap<Population, Double> selectEmployBeeProbabilityMap = setSelectEmployBeeProbability(employBeeList);

        List<Pair<Population, Double>> distributionList = new ArrayList<>();
        selectEmployBeeProbabilityMap.forEach((population, fitnessValue) -> distributionList.add(Pair.create(population, fitnessValue)));
        EnumeratedDistribution em = new EnumeratedDistribution<>(distributionList);

        int onlookerBeeInd = 0;
        while (onlookerBeeInd < onlookerBeeNum) {
            Population onlookerBee = (Population)em.sample(); //Onlooker bee init (from the one of employ bees)

            ArrayList<Double> old_param = onlookerBee.getParameterList();
            ArrayList<Double> new_param = new ArrayList<>();

            List<Double> newParameterListtmp = old_param.stream()
                    .map(this::updateFormula)
                    .collect(Collectors.toList());
            new_param.addAll(newParameterListtmp);

            double fitness =  getFitness(new_param);
            checkIsAlive(fitness, onlookerBee, new_param);

            Population p = employBeeList.get(employBeeList.indexOf(onlookerBee));
            employBeeList.set(employBeeList.indexOf(onlookerBee), p);

            onlookerBeeInd++;
        }

        return employBeeList;
    }

    private HashMap<Population, Double> setSelectEmployBeeProbability(ArrayList<Population> employBeeList){
        double totalFinessValue = calcEmployBeeFitnessSum(employBeeList);
        //Fitness value divide sum, get probability
        Map<Population, Double> probabilityMap= employBeeList.stream()
                .collect(Collectors.toMap(population->population,population->div(population.getFitnessValue(),totalFinessValue)));

        return  new HashMap<>(probabilityMap);
    }

    private double calcEmployBeeFitnessSum(ArrayList<Population> employBeeList){
        //Fitness value sum
        return employBeeList.stream()
                .mapToDouble(Population::getFitnessValue)
                .sum();
    }

    // fully random bee
    private ArrayList<Population> scoutBeeFlyingProcess(ArrayList<Population> employBeeList, int totalPopulation, int onlookerBeeNum){
        int scoutBeeInd = totalPopulation - employBeeList.size() - onlookerBeeNum;
        while (scoutBeeInd > 0) {
            ArrayList<Double> param = getRandomParameters();
            double fitness =  getFitness(param);

            if(!accuracyMode){
                Population minP = employBeeList.stream()
                        .min(Comparator.comparing(Population::getFitnessValue))
                        .orElse(null);

                if(!Objects.isNull(minP)){
                    employBeeList.get(employBeeList.indexOf(minP)).setAllParameter(param, fitness);
                }

                scoutBeeInd--;
                continue;
            }

            if(employBeeList.size() < div(totalPopulation * scoutBeePercent, 10)){
                employBeeList.add(new Population(param, fitness));
            }else {
                Population minP = employBeeList.stream()
                        .min(Comparator.comparing(Population::getFitnessValue))
                        .orElse(null);

                if(!Objects.isNull(minP)){
                    employBeeList.get(employBeeList.indexOf(minP)).setAllParameter(param, fitness);
                }
            }

            scoutBeeInd--;
        }

        return employBeeList;
    }

    private double updateFormula(double old_value){
        double alpha = div(new Random().nextInt(1000), 1000);
        double parameterRnd = div(new Random().nextInt(1000), 1000);

        double parameterResult = (old_value + mul(alpha, (old_value - parameterRnd)));
        return parameterResult > 0 ? (round(parameterResult) <= 1 ? round(parameterResult) : 1) : 0.0001 ;
    }
}
