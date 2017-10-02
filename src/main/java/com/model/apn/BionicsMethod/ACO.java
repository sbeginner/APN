package com.model.apn.BionicsMethod;

import com.model.apn.BionicsContainer.Population;
import com.model.apn.Model.APN;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ACO extends BionicHelper implements Bionics{
    private double[][] pheromoneMatrix;

    public ACO(int iterative, int totalPopulation, boolean accuracyMode){
        //Scout bee can be calculated
        this.iterative = iterative;
        this.totalPopulation = totalPopulation;
        this.accuracyMode = accuracyMode;
    }

    @Override
    public ArrayList<Population> bionicsMethod(APN apn, int curfoldInd, ArrayList<Population> PopulationList) {
        setAPN(apn);

        this.pheromoneMatrix = initPheromoneMatrix();

        PopulationList = updateParametersPolicy(PopulationList);

        return new ArrayList<>(PopulationList.stream()
                .filter(Population::isAlive)
                .collect(Collectors.toList()));
    }

    private double[][] initPheromoneMatrix(){
        this.pheromoneMatrix = new double[1000][1000];
        for(int i=0; i<1000; i++){
            for(int j=0; j<1000; j++){
                pheromoneMatrix[i][j] = 0.0001;
            }
        }
        return pheromoneMatrix;
    }

    @Override
    public ArrayList<Population> updateParametersPolicy(ArrayList<Population>  PopulationList){

        if(PopulationList.isEmpty()){
            PopulationList = setPopulation(totalPopulation);
        }

        PopulationList.forEach(ant -> {
            ArrayList<Double> old_param = ant.getParameterList();
            ArrayList<Double> new_param = new ArrayList<>();

            // create new parameters, choose the new parameters (1-by-1) by using pheromone-matrix
            for(double item: old_param){
                List<Pair<Integer, Double>> distributionList = new ArrayList<>();
                for (int i = 0; i < pheromoneMatrix[0].length; i++){
                    distributionList.add(
                            // pair( param-value * 1000, probability)
                            Pair.create(i, pheromoneMatrix[(int)(item*1000)][i])
                    );
                }
                EnumeratedDistribution em = new EnumeratedDistribution<>(distributionList);
                new_param.add((int)(em.sample())/1000.0);
            }

            double fitness =  getFitness(new_param);
            checkIsAlive(fitness, ant, new_param);

            // update pheromone-matrix
            for(int i = 0; i < ant.getParameterList().size(); i++){
                int from = (int)(old_param.get(i)*1000);
                int to = (int)(new_param.get(i)*1000);
                pheromoneMatrix[from][to] = updateFormula(pheromoneMatrix[from][to], fitness);
            }
        });

        return PopulationList;
    }

    double updateFormula(double old_value, double fitness){
        return (0.9)*old_value + fitness;
    }
}
