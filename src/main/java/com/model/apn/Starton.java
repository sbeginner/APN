package com.model.apn;

import DataStructure.Attribute;
import DataStructure.Instances;
import FileIO.DataInput;
import Preprocess.Filter;
import Preprocess.MEPA;
import com.model.apn.BionicsMethod.ABC;
import com.model.apn.BionicsMethod.ACO;
import com.model.apn.BionicsMethod.PSO;
import com.model.apn.Eval.Evaluation;
import com.model.apn.Model.APN;
import com.model.apn.Setup.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2017/3/20.
 */
public class Starton {

    public static void main(String str[]) throws IOException {
        setConfig();
        //crossValidation();
        bioCrossValidation();
        //forTrainTest();
    }

    private static void crossValidation() throws IOException {
        DataInput dt =new DataInput();
        dt.forKfoldValidationInstance();

        //dt.completeData();
        Instances instances = dt.getInstances();    //get data
        //instances.setRandSeed(1);
        //instances.autoShuffleInstanceOrder();
        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(new APN(), 10);
    }

    private static void bioCrossValidation() throws IOException {
        DataInput dt =new DataInput();
        dt.forKfoldValidationInstance();

        Instances instances = dt.getInstances();    //get data
        //instances.setRandSeed(1);
        //instances.autoShuffleInstanceOrder();
        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);

//        ABC abc = new ABC(10, 10, false);
//        abc.setDifferentBeePercent(0.3, 0.4, 0.5);
//        ACO abc = new ACO(10, 10, false);
        PSO abc = new PSO(10, 100, false);
        eval.crossValidateModel(new APN(), 10, abc);
    }

    private static void forTrainTest() throws IOException {
        DataInput dt =new DataInput();
        dt.forTrainTestInstance();
        //dt.completeData();
        Instances instances = dt.getInstances();    //get data
        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);
        eval.evalTrainTestModel(new APN());

    }

    private static void setConfig(){
        new Config();
    }
}
