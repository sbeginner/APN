package com.model.apn;

import DataStructure.Attribute;
import DataStructure.Instances;
import FileIO.DataInput;
import Preprocess.Filter;
import Preprocess.MEPA;
import com.model.apn.BionicsMethod.ABC;
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
        //forTrainTesttset();

        if(true){
            if(true)
                crossValidation();
            else
                bioCrossValidation();
        }
        else
        forTrainTest();
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
        eval.crossValidateModel(new APN(), instances, 10);
    }

    private static void bioCrossValidation() throws IOException {
        DataInput dt =new DataInput();
        dt.forKfoldValidationInstance();

        //dt.completeData();
        Instances instances = dt.getInstances();    //get data
        //instances.setRandSeed(1);
        //instances.autoShuffleInstanceOrder();
        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);

        ABC abc = new ABC(1000, 100, false);
        abc.setDifferentBeePercent(0.3, 0.4, 0.5);

        eval.crossValidateModel(new APN(), instances, 10, abc);
    }

    private static void forTrainTest() throws IOException {
        DataInput dt =new DataInput();
        dt.forTrainTestInstance();
        //dt.completeData();
        Instances instances = dt.getInstances();    //get data
        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);
        eval.evalTrainTestModel(new APN(), instances);

    }




    private static void forTrainTesttset() throws IOException {
        DataInput dt =new DataInput();
        dt.forTrainTestInstance();
        //dt.completeData();
        Instances instances = dt.getInstances();    //get data
        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);
        APN apn = new APN();
        MEPA mp = new MEPA();
        Instances mepaInstances = Filter.useFilter(instances, mp);
        eval.evalAPNProcesstest(apn, mepaInstances, 0, null);
        eval.toMatrixString(apn);


        dt.forTestInstance("C:/Data/test/test_USED.txt");
        instances = dt.getInstances();    //get data
        mepaInstances = Filter.useFilter(instances, mp);
        //model do something
        eval.evalAPNProcesstest(apn, mepaInstances, 0, null);
        //result
        eval.toMatrixString(apn);

        dt.forTestInstance("C:/Data/test/test_USED.txt");
        instances = dt.getInstances();    //get data
        mepaInstances = Filter.useFilter(instances, mp);
        //model do something
        eval.evalAPNProcesstest(apn, mepaInstances, 0, null);
        //result
        eval.toMatrixString(apn);

        dt.forTestInstance("C:/Data/test/test_USED.txt");
        instances = dt.getInstances();    //get data
        mepaInstances = Filter.useFilter(instances, mp);
        //model do something
        eval.evalAPNProcesstest(apn, mepaInstances, 0, null);
        //result
        eval.toMatrixString(apn);

        dt.forTestInstance("C:/Data/test/test_USED.txt");
        instances = dt.getInstances();    //get data
        mepaInstances = Filter.useFilter(instances, mp);
        //model do something
        eval.evalAPNProcesstest(apn, mepaInstances, 0, null);
        //result
        eval.toMatrixString(apn);
    }

    private static void setConfig(){
        new Config();
    }
}
