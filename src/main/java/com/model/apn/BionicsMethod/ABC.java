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
    private int iterative;

    private int totalPopulation;
    private double employBeePercent = 0.1;
    private double onlookerBeePercent = 0.4;
    private double scoutBeePercent = 1 - (employBeePercent + onlookerBeePercent);

    private boolean accuracyMode = false;

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

    public int getIterative(){
        return iterative;
    }

    @Override
    public Population getCurrentGlobalBestParameters(ArrayList<Population> employBeeList) {
        return employBeeList.stream()
                .max(Comparator.comparing(Population::getFitnessValue))
                .orElse(setEmployBee(1).get(0));
    }

    @Override
    public ArrayList<Population> bionicsMethod(APN apn, int randseed, ArrayList<Population> employBeeList) {
        this.initRandomSeed = RANDOM_SEED + (randseed + 1);
        System.out.println(initRandomSeed);
        this.apn = apn;

        int employBeeNum = (int) mul(totalPopulation, employBeePercent);
        int onlookerBeeNum = (int) mul(totalPopulation, onlookerBeePercent);

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

        if(!accuracyMode){
            return employBeeList;
        }

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

            employBeeList.set(employBeeList.indexOf(onlookerBee), p);

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

            if(!accuracyMode){
                Population minP = employBeeList.stream()
                        .min(Comparator.comparing(Population::getFitnessValue))
                        .orElse(null);

                if(!Objects.isNull(minP)){
                    employBeeList.get(employBeeList.indexOf(minP)).setAllParameter(parameterListtmp, fitnessFunc(averageMSE));
                }

                scoutBeeInd--;
                continue;
            }

            if(employBeeList.size() < div(totalPopulation * scoutBeePercent, 10)){
                employBeeList.add(new Population(parameterListtmp, fitnessFunc(averageMSE)));
            }else {
                Population minP = employBeeList.stream()
                        .min(Comparator.comparing(Population::getFitnessValue))
                        .orElse(null);

                if(!Objects.isNull(minP)){
                    employBeeList.get(employBeeList.indexOf(minP)).setAllParameter(parameterListtmp, fitnessFunc(averageMSE));
                }
            }

            scoutBeeInd--;
        }

        return employBeeList;
    }

    private double ABCConditonalRandomPosition(double parameter, int randomSeed){
        double r = div(new Random(randomSeed).nextInt(1000), 1000);
        double parameterRnd = div(new Random(randomSeed+(int)parameter).nextInt(1000), 1000);

        double parameterResult = (parameter + mul(r, (parameter - parameterRnd)));

        return parameterResult > 0 ? (round(parameterResult) <= 1 ? round(parameterResult) : 1) : 0.0001 ;
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
