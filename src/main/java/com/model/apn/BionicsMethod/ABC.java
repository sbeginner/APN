package com.model.apn.BionicsMethod;

import com.model.apn.BionicsContainer.Population;
import com.model.apn.Model.APN;
import com.sun.jmx.snmp.Enumerated;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.distribution.EnumeratedDistribution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.div;
import static MathCalculate.Arithmetic.mul;
import static MathCalculate.Arithmetic.round;
import static Setup.Config.INSTANCE_NUM;
import static Setup.Config.MAX_FOLDNUM;
import static Setup.Config.RANDOM_SEED;

/**
 * Created by JACK on 2017/5/14.
 */
public class ABC implements Bionics{
    private int initRandomSeed;
    private APN apn;

    public ABC(){

    }

    @Override
    public Population getCurrentGlobalBestParameters(ArrayList<Population> employBeeList) {
        return employBeeList.stream()
                .max(Comparator.comparing(Population::getFitnessValue))
                .orElse(setEmployBee(1).get(0));
    }

    @Override
    public ArrayList<Population> bionicsMethod(APN apn, int curfoldInd, ArrayList<Population> employBeeList) {
        this.initRandomSeed = (int)(RANDOM_SEED * MAX_FOLDNUM * INSTANCE_NUM * (0.87)) & (curfoldInd + 1);
        this.apn = apn;

        System.out.println(curfoldInd);

        int totalPopulation = 100;
        int employBeeNum = 10;
        int onlookerBeeNum = 40;

        if(employBeeList.isEmpty()){
            employBeeList = setEmployBee(employBeeNum);
        }

        employBeeList = employBeeFlyingProcess(employBeeList);
        employBeeList = onlookerBeeFlyingProcess(employBeeList, onlookerBeeNum);
        employBeeList = scoutBeeFlyingProcess(employBeeList, totalPopulation, onlookerBeeNum);

        return new ArrayList<>(employBeeList.stream()
                .filter(Population::isAlive)
                .collect(Collectors.toList()));
    }

    private ArrayList<Population> employBeeFlyingProcess(ArrayList<Population> employBeeList){

        employBeeList.stream().forEach(employBee -> {

            ArrayList<Double> parameterListtmp = employBee.getParameterList();

            List<Double> newParameterListtmp = parameterListtmp.stream()
                    .map(parameter -> ABCConditonalRandomPosition(parameter, initRandomSeed))
                    .collect(Collectors.toList());
            ArrayList newParameterList = new ArrayList<>(newParameterListtmp);

            this.apn.setBionicsParameters(newParameterList);
            double averageMSE = this.apn.travelBionicsAPNmodel();

            if(fitnessFunc(averageMSE) > employBee.getFitnessValue()){
                employBee.setAllParameter(newParameterList, fitnessFunc(averageMSE));
                employBee.resetAliveTime();
            }else {
                employBee.decreaseAliveTime();
            }
        });

        return employBeeList;
    }

    private ArrayList<Population> onlookerBeeFlyingProcess(ArrayList<Population> employBeeList, int onlookerBeeNum){
        HashMap<Population, Double> selectEmployBeeProbabilityMap = setSelectEmployBeeProbability(employBeeList);

        List<Pair<Population, Double>> distributionList = new ArrayList<>();
        selectEmployBeeProbabilityMap.forEach((population, fitnessValue) -> distributionList.add(Pair.create(population, fitnessValue)));
        EnumeratedDistribution em = new EnumeratedDistribution<>(distributionList);
        em.reseedRandomGenerator(initRandomSeed);


        int onlookerBeeInd = 0;
        while (onlookerBeeInd < onlookerBeeNum) {
            Population onlookerBee = (Population)em.sample(); //Onlooker bee init (from the one of employ bees)
            ArrayList<Double> parameterListtmp = onlookerBee.getParameterList();

            List<Double> newParameterListtmp = parameterListtmp.stream()
                    .map(parameter -> ABCConditonalRandomPosition(parameter, initRandomSeed))
                    .collect(Collectors.toList());

            ArrayList<Double> newParameterList = new ArrayList<>(newParameterListtmp);

            this.apn.setBionicsParameters(newParameterList);
            double averageMSE = this.apn.travelBionicsAPNmodel();
            Population p = employBeeList.get(employBeeList.indexOf(onlookerBee));

            if(fitnessFunc(averageMSE) > onlookerBee.getFitnessValue()){
                p.setAllParameter(newParameterList, fitnessFunc(averageMSE));
                p.resetAliveTime();
            }else {
                p.decreaseAliveTime();
            }

            onlookerBeeInd++;
        }

        return employBeeList;
    }

    private ArrayList<Population> scoutBeeFlyingProcess(ArrayList<Population> employBeeList, int totalPopulation, int onlookerBeeNum){
        int scoutBeeNum = totalPopulation - employBeeList.size() - onlookerBeeNum;

        int scoutBeeInd = scoutBeeNum;
        while (scoutBeeInd > 0) {
            ArrayList<Double> parameterListtmp = this.apn.setBionicsParameters(this.initRandomSeed , scoutBeeInd);
            double averageMSE = this.apn.travelBionicsAPNmodel();

            Population minP = employBeeList.stream()
                    .min(Comparator.comparing(Population::getFitnessValue))
                    .orElse(null);

            if(!Objects.isNull(minP)){
                employBeeList.get(employBeeList.indexOf(minP))
                        .setAllParameter(parameterListtmp, fitnessFunc(averageMSE));
            }

            scoutBeeInd--;
        }

        return employBeeList;
    }

    private double ABCConditonalRandomPosition(double parameter, int randomSeed){
        double r = new Random(randomSeed).nextDouble();
        double parameterRnd = new Random(randomSeed+(int)parameter).nextDouble();

        double parameterResult = (parameter + mul(r, (parameter - parameterRnd)));

        return parameterResult > 0 ? round(parameterResult) : 0.0001 ;
    }

    private double calcEmployBeeFitnessSum(ArrayList<Population> employBeeList){
        //Fitness value sum
        return employBeeList.stream()
                .mapToDouble(Population::getFitnessValue)
                .sum();
    }

    private HashMap<Population, Double> setSelectEmployBeeProbability(ArrayList<Population> employBeeList){
        double totalFinessValue = calcEmployBeeFitnessSum(employBeeList);
        //Fitness value divide sum, get probability
        Map<Population, Double> probabilityMap= employBeeList.stream()
                .collect(Collectors.toMap(population->population,population->div(population.getFitnessValue(),totalFinessValue)));

        return  new HashMap<>(probabilityMap);
    }

    private ArrayList<Population> setEmployBee(int employBeeNum){
        ArrayList<Population> employBeeList = new ArrayList<>();

        IntStream.range(0, employBeeNum).forEach(employBeeInd -> {
            ArrayList<Double> arrayListtmp = this.apn.setBionicsParameters(this.initRandomSeed , employBeeInd);
            double averageMSE = this.apn.travelBionicsAPNmodel();

            double abcFitness = fitnessFunc(averageMSE);
            employBeeList.add(new Population(arrayListtmp, abcFitness));
        });

        return employBeeList;
    }

    @Override
    public double fitnessFunc(double fitnessValue) {
        return div(1, fitnessValue + 1);
    }

}
