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

import static Setup.Config.INSTANCEORDER_SHUFFLE_BTN;
import static Setup.Config.MAX_FOLDNUM;
import static Setup.Config.TARGET_ATTRIBUTE;

/**
 * Created by jack on 2017/3/20.
 */
class Cmd {

    Cmd(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        setConfig(args);
        forTrainTestAdaptor(args);
        crossValidationAdaptor(args);

        System.out.println("we spend "+(double) (System.currentTimeMillis() - start)/1000+" second");
    }

    private static void setConfig(String[] args){
        if("-CV".equals(args[0])){
            new Config(args, true);
        }else if("-TT".equals(args[0])) {
            new Config(args, false);
        }else {
            System.out.println("Invalid command...");
        }
    }


    private static void forTrainTestAdaptor(String[] args) throws IOException {
        if (!"-TT".equals(args[0])){
            return;
        }

        if (args.length < 6){
            forTrainTest();
        }else {
            switch (args[5]){
                case "-ABC":
                    bioForTrainTestABC(args);
                    break;
                case "-ACO":
                    bioForTrainTestACO(args);
                    break;
                case "-PSO":
                    bioForTrainTestPSO(args);
                    break;
            }
        }
    }

    private static Evaluation forTrainTestDataProcess() throws IOException {
        DataInput dt =new DataInput();
        dt.forTrainTestInstance();
        Instances instances = dt.getInstances();
        return new Evaluation(instances);
    }

    private static void forTrainTest() throws IOException {
        Evaluation eval = forTrainTestDataProcess();
        eval.evalTrainTestModel(new APN());
    }

    private static void bioForTrainTestABC(String[] args) throws IOException {
        Evaluation eval = forTrainTestDataProcess();
        ABC abc_method = new ABC(Integer.valueOf(args[6]),  Integer.valueOf(args[7]), true);
        String[] parameters = args[8].split(":");
        abc_method.setDifferentBeePercent(Double.valueOf(parameters[0]), Double.valueOf(parameters[1]), Double.valueOf(parameters[2]));
        eval.evalTrainTestModel(new APN(), abc_method);
    }

    private static void bioForTrainTestACO(String[] args) throws IOException {
        Evaluation eval = forTrainTestDataProcess();
        ACO aco_method = new ACO(Integer.valueOf(args[6]),  Integer.valueOf(args[7]), true);
        eval.evalTrainTestModel(new APN(), aco_method);
    }

    private static void bioForTrainTestPSO(String[] args) throws IOException {
        Evaluation eval = forTrainTestDataProcess();
        PSO pso_method = new PSO(Integer.valueOf(args[6]),  Integer.valueOf(args[7]), true);
        eval.evalTrainTestModel(new APN(), pso_method);
    }


    private static void crossValidationAdaptor(String[] args) throws IOException {
        if (!"-CV".equals(args[0])){
            return;
        }

        if (args.length < 6){
            crossValidation();
        }else {
            switch (args[5]){
                case "-ABC":
                    bioCrossValidationABC(args);
                    break;
                case "-ACO":
                    bioCrossValidationACO(args);
                    break;
                case "-PSO":
                    bioCrossValidationPSO(args);
                    break;
            }
        }
    }

    private static Evaluation crossValidationDataProcess() throws IOException {
        DataInput dt = new DataInput();
        dt.forKfoldValidationInstance();
        Instances instances = dt.getInstances();

        INSTANCEORDER_SHUFFLE_BTN = true;
        instances.setRandSeed(1);
        instances.autoShuffleInstanceOrder();

        return new Evaluation(instances);
    }

    private static void crossValidation() throws IOException {
        Evaluation eval = crossValidationDataProcess();
        eval.crossValidateModel(new APN(), MAX_FOLDNUM);
    }

    private static void bioCrossValidationABC(String[] args) throws IOException {
        String[] parameters = args[8].split(":");

        Evaluation eval = crossValidationDataProcess();
        ABC abc_method = new ABC(Integer.valueOf(args[6]),  Integer.valueOf(args[7]), true);
        abc_method.setDifferentBeePercent(Double.valueOf(parameters[0]), Double.valueOf(parameters[1]), Double.valueOf(parameters[2]));
        eval.crossValidateModel(new APN(), MAX_FOLDNUM, abc_method);
    }

    private static void bioCrossValidationACO(String[] args) throws IOException {
        Evaluation eval = crossValidationDataProcess();
        ACO aco_method = new ACO(Integer.valueOf(args[6]),  Integer.valueOf(args[7]), true);
        eval.crossValidateModel(new APN(), MAX_FOLDNUM, aco_method);
    }

    private static void bioCrossValidationPSO(String[] args) throws IOException {
        Evaluation eval = crossValidationDataProcess();
        PSO pso_method = new PSO(Integer.valueOf(args[6]),  Integer.valueOf(args[7]), true);
        eval.crossValidateModel(new APN(), MAX_FOLDNUM, pso_method);
    }

}
