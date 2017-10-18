package com.model.apn;

import DataStructure.Instances;
import FileIO.DataInput;
import com.model.apn.BionicsMethod.ABC;
import com.model.apn.BionicsMethod.ACO;
import com.model.apn.BionicsMethod.PSO;
import com.model.apn.Eval.Evaluation;
import com.model.apn.Model.APN;
import com.model.apn.Setup.Config;

import java.io.IOException;

import static Setup.Config.MAX_FOLDNUM;

/**
 * Created by jack on 2017/3/20.
 */
public class Starton {

    public static void main(String[] args) throws IOException {
//        System.out.println(str[0]);
        /*
        IF [CV]
            args[0]: is CV
            args[1]: dir_path
            args[2]: file_name
            args[3]: max_fold
            args[4]: MEPA_divide_num
            args[5]: args => (-O, -ABC, -ACO, -PSO)

            args[?]: correlationNet:min_branch:min_correlation

            ex. "java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5"
        END IF

         */

        setConfig(args);

        switch (args[5]){
            case "-ABC":
                bioCrossValidationABC();
                break;
            case "-ACO":
                bioCrossValidationACO();
                break;
            case "-PSO":
                bioCrossValidationPSO();
                break;
            case "-O":
                crossValidation();
                break;
        }


        //forTrainTest();
    }

    private static void crossValidation() throws IOException {
        Evaluation eval = crossValidationDataProcess();
        eval.crossValidateModel(new APN(), MAX_FOLDNUM);
    }

    private static void bioCrossValidationABC() throws IOException {
        Evaluation eval = crossValidationDataProcess();
        ABC abc_method = new ABC(10, 10, true);
        abc_method.setDifferentBeePercent(0.3, 0.4, 0.5);
        eval.crossValidateModel(new APN(), MAX_FOLDNUM, abc_method);
    }

    private static void bioCrossValidationACO() throws IOException {
        Evaluation eval = crossValidationDataProcess();
        ACO aco_method = new ACO(10, 10, true);
        eval.crossValidateModel(new APN(), MAX_FOLDNUM, aco_method);
    }

    private static void bioCrossValidationPSO() throws IOException {
        Evaluation eval = crossValidationDataProcess();
        PSO pso_method = new PSO(10, 10, true);
        eval.crossValidateModel(new APN(), MAX_FOLDNUM, pso_method);
    }

    private static Evaluation crossValidationDataProcess() throws IOException {
        DataInput dt = new DataInput();
        dt.forKfoldValidationInstance();
        Instances instances = dt.getInstances();
        return new Evaluation(instances);
    }


//    private static void forTrainTest() throws IOException {
//        DataInput dt =new DataInput();
//        dt.forTrainTestInstance();
//        Instances instances = dt.getInstances();    //get data
//        Evaluation eval = new Evaluation(instances);
//        ABC abc = new ABC(10, 10, false);
//        abc.setDifferentBeePercent(0.3, 0.4, 0.5);
//        eval.evalTrainTestModel(new APN(), abc);
//    }

    private static void setConfig(String[] args){
        if("-CV".equals(args[0])){
            new Config(args);
        }
    }
}
