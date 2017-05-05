package com.model.apn;

import DataStructure.Instances;
import FileIO.DataInput;
import com.model.apn.Eval.Evaluation;
import com.model.apn.Model.APN;

import java.io.IOException;

import static Setup.Config.RANDOM_SEED;
/**
 * Created by jack on 2017/3/20.
 */
public class Starton {

    public static void main(String str[]) throws IOException {
        DataInput dt =new DataInput();
        dt.forKfoldValidationInstance();
        //dt.forTrainTestInstance();
        dt.completeData();
        Instances instances = dt.getInstances();    //get data

        ///////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(new APN(), instances, 10, RANDOM_SEED);
        //eval.evalTrainTestModel(new APN(), instances, RANDOM_SEED);

        /*
        System.out.println(dt.getInstances().getCurrentMode());
        System.out.println();
        System.out.println("Attribute size : "+dt.getInstances().getAttributeMap().size());
        System.out.println();
        */

    }



}
