package com.model.apn;

import DataStructure.Instances;
import FileIO.DataInput;
import com.model.apn.Eval.Evaluation;
import com.model.apn.Model.APN;
import com.model.apn.Setup.Config;

import java.io.IOException;

import static com.model.apn.Setup.Config.RANDOM_SEED;
/**
 * Created by jack on 2017/3/20.
 */
public class Starton {

    public static void main(String str[]) throws IOException {
        resetConfig();
        if(true)
        crossValidation();
        else
        forTrainTest();
    }

    private static void crossValidation() throws IOException {
        DataInput dt =new DataInput();
        dt.forKfoldValidationInstance();
        //dt.completeData();
        Instances instances = dt.getInstances();    //get data
        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(new APN(), instances, 10, RANDOM_SEED);
    }

    private static void forTrainTest() throws IOException {

        DataInput dt =new DataInput();
        dt.forTrainTestInstance();
        //dt.completeData();
        Instances instances = dt.getInstances();    //get data
        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);
        eval.evalTrainTestModel(new APN(), instances, RANDOM_SEED);
    }

    private static void resetConfig(){
        new Config();
    }


}
