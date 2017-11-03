package com.model.apn.Eval;

import DataStructure.Attribute;
import DataStructure.Instances;
import com.model.apn.Container.APNOutputInstanceInfo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.*;
import static Setup.Config.NONVALUE_INTEGER;
import static Setup.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Setup.Config.PRINT_DETAIL_BTN;

/**
 * Created by JACK on 2017/5/14.
 */
public class ConfusionMatrix {
    private Instances instances;
    private int targetValueNum = NONVALUE_INTEGER;
    private int maxTargetValueStingLen = NONVALUE_INTEGER;
    private Attribute targetAttribute;
    private int[][] confusionMatrix;

    public ConfusionMatrix(Instances instances){
        this.instances = instances;
        init();
    }

    private void init(){
        targetValueNum = instances.getAttribute(TARGET_ATTRIBUTE).getAllValue().size();
        this.targetAttribute = instances.getAttribute(TARGET_ATTRIBUTE);
        this.maxTargetValueStingLen = targetAttribute.getAllValue()
                .stream()
                .mapToInt(StringBuilder::length)
                .max()
                .orElse(-1) + 1;
    }

    public void setConfusionMatrix(ArrayList<APNOutputInstanceInfo> APNOutputInstanceInfoList) {
        int[][] confusionMatrix = new int[targetValueNum][targetValueNum];

        APNOutputInstanceInfoList.forEach(outputInstanceInfo -> {
            int predictInd = targetAttribute.getAttrValueIndByString(outputInstanceInfo.getAPNPredict());
            int realInd = targetAttribute.getAttrValueIndByString(outputInstanceInfo.getRealTargetValue());
            confusionMatrix[predictInd][realInd] += 1;
        });

        this.confusionMatrix = confusionMatrix;

        printConfusionMatrixInfo(confusionMatrix);
    }

    public void printConfusionMatrixInfo(){
        int[][] confusionMatrix = this.confusionMatrix;
        printConfusionMatrixInfo(confusionMatrix);
    }

    private void printConfusionMatrixInfo(int[][] confusionMatrix){
        if(!PRINT_DETAIL_BTN){
            return;
        }

        printConfusionMatrix(confusionMatrix);
        printIndicators(confusionMatrix);
    }

    public void printConfusionMatrixInfoForce(int[][] confusionMatrix){
        printConfusionMatrix(confusionMatrix);
        printIndicators(confusionMatrix);
    }

    private void printConfusionMatrix(int[][] confusionMatrix){
        System.out.println();
        System.out.println("<---- Confusion Matrix ---->");
        System.out.println();
        System.out.format("%-"+maxTargetValueStingLen+"s","");
        targetAttribute.getAllValue().forEach(targetValue -> System.out.format("%"+maxTargetValueStingLen+"s ",targetValue.toString()));
        System.out.println(" "+"<- [real]");

        IntStream.range(0, confusionMatrix.length)
                .forEach(nRow -> {
                    System.out.format("%-"+maxTargetValueStingLen+"s:",targetAttribute.getAttrValueStrByIndex(nRow));
                    IntStream.range(0, confusionMatrix[nRow].length)
                            .forEach(nCol-> System.out.format("%"+maxTargetValueStingLen+"s ",confusionMatrix[nRow][nCol]+" "));
                    System.out.println();
                });
    }

    private void printIndicators(int[][] confusionMatrix){
        int testInstanceNum;
        int[] MatrixColumn = new int[targetValueNum];
        int[] MatrixRow = new int[targetValueNum];
        int[] TruePositive = new int[targetValueNum];
        int[] FalsePositive = new int[targetValueNum];
        int[] TrueNegative = new int[targetValueNum];
        int[] FalseNegative = new int[targetValueNum];

        IntStream.range(0, confusionMatrix.length)
                .forEach(nRow -> IntStream.range(0, confusionMatrix[nRow].length)
                        .forEach(nCol -> {
                            if (nRow == nCol) {
                                TruePositive[nRow] = confusionMatrix[nRow][nCol];
                            }
                            MatrixRow[nRow] += confusionMatrix[nRow][nCol];
                            MatrixColumn[nCol] += confusionMatrix[nRow][nCol];
                        }));

        testInstanceNum = Arrays.stream(MatrixRow).sum(); // or MatrixColumn

        IntStream.range(0, targetValueNum)
                .forEach(nCol -> {
                    FalsePositive[nCol] += MatrixRow[nCol] - TruePositive[nCol];
                    FalseNegative[nCol] += MatrixColumn[nCol] - TruePositive[nCol];
                    TrueNegative[nCol] += testInstanceNum - FalseNegative[nCol] - FalsePositive[nCol] - TruePositive[nCol];
                });

        printIndicatorsValue(TruePositive, TrueNegative, FalsePositive, FalseNegative, testInstanceNum, 0);
    }

    private void printIndicatorsValue(int[] TruePositive, int[] TrueNegative, int[] FalsePositive, int[] FalseNegative, int testInstanceNum, int interestTargetValueInd){
        System.out.println();
        System.out.println(">> Target [ "+targetAttribute.getAttrValueStrByIndex(interestTargetValueInd)+" ]");
        System.out.println("TP: "+TruePositive[interestTargetValueInd]+
                " TN: "+TrueNegative[interestTargetValueInd]+
                " FP: "+FalsePositive[interestTargetValueInd]+
                " FN: "+FalseNegative[interestTargetValueInd]);
        System.out.println("[ Accuracy ] => " + calcAccuracy(TruePositive, testInstanceNum));
        System.out.println("[ Sensitivity ] => " + calcSensitivity(TruePositive, FalseNegative)[interestTargetValueInd]);
        System.out.println("[ Specificity ] => " + calcSpecificity(TrueNegative, FalsePositive)[interestTargetValueInd]);
        System.out.println("[ Precision ] => " + calcPrecision(TruePositive, FalsePositive)[interestTargetValueInd]);
        System.out.println("[ Recall ] => " + calcRecall(TruePositive, FalseNegative)[interestTargetValueInd]);
        System.out.println("[ F1-measure ] => " + calcF1score(calcPrecision(TruePositive, FalsePositive), calcRecall(TruePositive, FalseNegative))[interestTargetValueInd]);
        System.out.println("[ G-mean ] => " + calcGmean(TruePositive, TrueNegative, FalsePositive, FalseNegative)[interestTargetValueInd]);
        System.out.println();
    }

    private double calcAccuracy(int[] TruePositive, int testInstanceNum){
        return div(IntStream.of(TruePositive).sum(), testInstanceNum);
    }

    private double[] calcSensitivity(int[] TruePositive, int[] FalseNegative){
        double[] sensitivity = new double[targetValueNum];
        IntStream.range(0, targetValueNum).forEach(nCol -> {
            sensitivity[nCol] = div(TruePositive[nCol], add(TruePositive[nCol],  FalseNegative[nCol]));
        });

        return sensitivity;
    }

    private double[] calcSpecificity(int[] TrueNegative, int[] FalsePositive){
        double[] sensitivity = new double[targetValueNum];
        IntStream.range(0, targetValueNum).forEach(nCol -> {
            sensitivity[nCol] = div(TrueNegative[nCol], add(TrueNegative[nCol],  FalsePositive[nCol]));
        });

        return sensitivity;
    }

    private double[] calcPrecision(int[] TruePositive, int[] FalsePositive){
        double[] precision = new double[targetValueNum];
        IntStream.range(0, targetValueNum).forEach(nCol -> {
            precision[nCol] = div(TruePositive[nCol], add(TruePositive[nCol],  FalsePositive[nCol]));
        });

        return precision;
    }

    private double[] calcRecall(int[] TruePositive, int[] FalseNegative){
        double[] recall = new double[targetValueNum];
        IntStream.range(0, targetValueNum).forEach(nCol -> {
            recall[nCol] = div(TruePositive[nCol], add(TruePositive[nCol],  FalseNegative[nCol]));
        });

        return recall;
    }

    private double[] calcF1score(double[] precision, double[] recall){

        double[] F1score = new double[targetValueNum];
        IntStream.range(0, targetValueNum).forEach(nCol -> {
            F1score[nCol] = div(mul(precision[nCol], recall[nCol]), add(precision[nCol], recall[nCol]));
            F1score[nCol] = mul(2, F1score[nCol]);
        });

        return F1score;
    }

    private double[] calcGmean(int[] TruePositive, int[] TrueNegative, int[] FalsePositive, int[] FalseNegative){

        double[] Gmean = new double[targetValueNum];
        IntStream.range(0, targetValueNum).forEach(nCol -> {
            double Gmean_l = div(TrueNegative[nCol], (TrueNegative[nCol]+FalsePositive[nCol]));
            double Gmean_r = div(TruePositive[nCol], (TruePositive[nCol]+FalseNegative[nCol]));
            Gmean[nCol] = sqrt(Gmean_l * Gmean_r);
        });

        return Gmean;
    }

    public int[][] getConfusionMatrix(){
        return confusionMatrix;
    }
}
