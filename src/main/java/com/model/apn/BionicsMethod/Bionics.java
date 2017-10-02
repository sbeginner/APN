package com.model.apn.BionicsMethod;

import com.model.apn.BionicsContainer.Population;
import com.model.apn.Model.APN;

import java.util.ArrayList;

/**
 * Created by JACK on 2017/5/14.
 */
public interface Bionics {
    
    ArrayList<Population> bionicsMethod(APN apn, int curfoldInd,  ArrayList<Population> PopulationList);

    ArrayList<Population> updateParametersPolicy(ArrayList<Population>  PopulationList);

    int getIterative();

    Population getCurrentGlobalBestParameters(ArrayList<Population> populationList);

    void setGlobalBestParameters(Population bestPopulation);
}
