package com.model.apn.Container;


import DataStructure.Instances;
import com.model.apn.Eval.ConfusionMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.div;
import static Setup.Config.*;
import static com.model.apn.Setup.Config.KEEP_TOTALRESULT_BY_EACH_FOLD;

/**
 * Created by JACK on 2017/5/12.
 */
public class APNOutputInfo {
    private Instances instances;
    private HashMap<Integer, ConfusionMatrix> confusionMatrixMap;
    private int targetValueNum = NONVALUE_INTEGER;


    public APNOutputInfo(Instances instances) {
        this.instances = instances;
        confusionMatrixMap = new HashMap<>(MAX_FOLDNUM);
        targetValueNum = instances.getAttribute(TARGET_ATTRIBUTE).getAllValue().size();
    }

    public double calcAverageMSE(double totalMSE){
        return div(totalMSE, INSTANCE_NUM_TRAIN);
    }

    public void setAPNOutputInstanceInfo(ArrayList<APNOutputInstanceInfo> APNOutputInstanceInfoList, int curfoldInd) {
        ConfusionMatrix confusionMatrix = new ConfusionMatrix(instances);
        confusionMatrix.setConfusionMatrix(APNOutputInstanceInfoList);

        if(KEEP_TOTALRESULT_BY_EACH_FOLD){
            confusionMatrixMap.put(curfoldInd, confusionMatrix);
        }else {
            confusionMatrixMap.put(autoIncreaseIndex(), confusionMatrix);
        }
    }

    private HashMap<Integer, ConfusionMatrix> getConfusionMatrixMap(){
        return confusionMatrixMap;
    }

    private int[][] combineConfusionMatrix(){

        int newConfusionMatrix[][] = new int[targetValueNum][targetValueNum];
        this.getConfusionMatrixMap()
                .values()
                .forEach(confusionMatrix -> IntStream.range(0, targetValueNum)
                        .forEach(nRow -> IntStream.range(0, targetValueNum)
                                .forEach(nCol -> newConfusionMatrix[nRow][nCol] += confusionMatrix.getConfusionMatrix()[nRow][nCol])));

        ConfusionMatrix confusionMatrix = new ConfusionMatrix(instances);
        confusionMatrix.printConfusionMatrixInfoForce(newConfusionMatrix);
        return newConfusionMatrix;
    }

    private int autoIncreaseIndex(){
        return confusionMatrixMap.size();
    }

    public void getEachConfusionMatrixOutput(){
        this.getConfusionMatrixMap()
                .values()
                .forEach(ConfusionMatrix::printConfusionMatrixInfo);
    }

    public int[][] getTotalConfusionMatrixOutput(){
        return this.combineConfusionMatrix();
    }
}
