package com.model.apn.BionicsMethod;

import MathCalculate.Arithmetic;
import com.model.apn.BionicsContainer.Population;
import com.model.apn.Model.APN;
import org.apache.commons.math3.analysis.function.Sigmoid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class PSO extends BionicHelper implements Bionics{

    public PSO(int iterative, int totalPopulation, boolean accuracyMode){
        //Scout bee can be calculated
        this.iterative = iterative;
        this.totalPopulation = totalPopulation;
        this.accuracyMode = accuracyMode;
    }

    @Override
    public ArrayList<Population> bionicsMethod(APN apn, int curfoldInd, ArrayList<Population> PopulationList) {
        setAPN(apn);

        if(curfoldInd == 0){
            PopulationList = updateParametersPolicyInit(PopulationList);
        }else {
            PopulationList = updateParametersPolicy(PopulationList);
        }

        return new ArrayList<>(PopulationList.stream()
                .filter(Population::isAlive)
                .collect(Collectors.toList()));
    }

    private ArrayList<Population> updateParametersPolicyInit(ArrayList<Population> PopulationList){

        if(PopulationList.isEmpty()){
            PopulationList = setPopulation(totalPopulation);
        }

        PopulationList.forEach(bird -> {
            ArrayList<Double> old_param = bird.getParameterList();
            bird.setSpeedList(new ArrayList<>(Collections.nCopies(old_param.size(), 0.0)));
            double fitness = getFitness(old_param);
            checkIsAlive(fitness, bird, old_param);
            bird.setLocalBestParameters();
        });

        return PopulationList;
    }

    @Override
    public ArrayList<Population> updateParametersPolicy(ArrayList<Population>  PopulationList){

        if(PopulationList.isEmpty()){
            PopulationList = setPopulation(totalPopulation);
        }

        ArrayList<Double> best_G  = getGlobalBestParameters().getParameterList();
        PopulationList.forEach((Population bird) -> {
            ArrayList<Double> best_L  = bird.getLocalBestParameterList();
            ArrayList<Double> old_param = bird.getParameterList();
            ArrayList<Double> speed_list = bird.getSpeedList();
            ArrayList<Double> new_param = new ArrayList<>();

            Random rnd = new Random();
            for(int i=0; i<old_param.size(); i++){
                double cur_speed = (.7)*speed_list.get(i);
                cur_speed += (.2)*(rnd.nextInt(1000)/1000)*(best_L.get(i) - old_param.get(i));
                cur_speed += (.8)*(rnd.nextInt(1000)/1000)*(best_G.get(i) - old_param.get(i));
                speed_list.set(i, cur_speed);
                new_param.add(Arithmetic.round(new Sigmoid().value(old_param.get(i) + cur_speed)));
            }

            double fitness = getFitness(new_param);
            checkIsAlive(fitness, bird, new_param);
            bird.setLocalBestParameters();
        });

        return PopulationList;
    }

}
