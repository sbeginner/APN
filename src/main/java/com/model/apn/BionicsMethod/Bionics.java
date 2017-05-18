package com.model.apn.BionicsMethod;

import com.model.apn.BionicsContainer.Population;
import com.model.apn.Model.APN;

import java.util.ArrayList;

/**
 * Created by JACK on 2017/5/14.
 */
public interface Bionics {
    Population getCurrentGlobalBestParameters(ArrayList<Population> employBeeList);
    ArrayList<Population> bionicsMethod(APN apn, int curfoldInd,  ArrayList<Population> employBeeList);
    double fitnessFunc(double fitnessValue);
}
