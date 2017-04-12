package com.model.apn;

import com.model.apn.DataStructure.Instances;
import com.model.apn.Eval.Evaluation;
import com.model.apn.FileIO.DataInput;
import com.model.apn.Model.APN;
import com.model.apn.Preprocess.Filter;
import com.model.apn.Preprocess.MEPA;

import java.io.IOException;

import static com.model.apn.Config.RANDOM_SEED;

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
        //Instances mepaInstances = Filter.useFilter(instances, new MEPA());


        //////////////////////////////////////////////////////////////////
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(new APN(), instances, 10, RANDOM_SEED);
        //eval.evalTrainTestModel(new APN(), instances, RANDOM_SEED);


        System.out.println(dt.getInstances().getCurrentMode());
        System.out.println();
        System.out.println("Attribute size : "+dt.getInstances().getAttributeMap().size());
        System.out.println();


        /*
        dt.getInstances().getTrainInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " +v.getInstanceValue(1));
        });
        dt.getInstances().getTestInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " +v.getInstanceValue(1));
        });
        */

        /*
        System.out.println(dt.getInstances().getTrainInstanceMap().size());
        dt.getInstances().getTrainInstance(0).getInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " + v);
        });
        System.out.println(dt.getInstances().getTestInstanceMap().size());
        dt.getInstances().getTestInstance(6).getInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " + v);
        });
        */
    }



}
