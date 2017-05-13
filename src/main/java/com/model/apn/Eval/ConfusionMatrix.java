package com.model.apn.Eval;

import DataStructure.Attribute;
import DataStructure.Instances;
import com.model.apn.Container.APNOutputInstanceInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import static MathCalculate.Arithmetic.add;
import static MathCalculate.Arithmetic.div;
import static MathCalculate.Arithmetic.mul;
import static Setup.Config.NONVALUE_INTEGER;
import static Setup.Config.TARGET_ATTRIBUTE;
import static MathCalculate.Arithmetic.sqrt;
import static com.model.apn.Setup.Config.PRINT_DETAIL_BTN;

/**
 * Created by JACK on 2017/5/14.
 */
public class ConfusionMatrix {
    private Instances instances;
    private int targetValueNum = NONVALUE_INTEGER;
    private int maxTargetValueStingLen = NONVALUE_INTEGER;
    private Attribute targetAttribute;
    private int interesTargetValueInd = 0;
    private int[][] confusionMatrix;

    public ConfusionMatrix(Instances instances){
        this.instances = instances;
        init();
    }

    public void init(){
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

        APNOutputInstanceInfoList.stream().forEach(outputInstanceInfo -> {
            int predictInd = targetAttribute.getAttrValueIndByString(outputInstanceInfo.getAPNPredict().toString());
            int realInd = targetAttribute.getAttrValueIndByString(outputInstanceInfo.getRealTargetValue());
            confusionMatrix[predictInd][realInd] += 1;
        });

        this.confusionMatrix = confusionMatrix;

        printConfusionMatrixInfo(confusionMatrix);
    }

    public void printConfusionMatrixInfo(){
        int[][] confusionMatrix = this.confusionMatrix;

        printConfusionMatrix(confusionMatrix);
        printIndicators(confusionMatrix);
    }

    public void printConfusionMatrixInfo(int[][] confusionMatrix){
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
        targetAttribute.getAllValue().stream().forEach(targetValue -> System.out.format("%"+maxTargetValueStingLen+"s ",targetValue.toString()));
        System.out.println();

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
                    FalseNegative[nCol] += MatrixRow[nCol] - TruePositive[nCol];
                    FalsePositive[nCol] += MatrixColumn[nCol] - TruePositive[nCol];
                    TrueNegative[nCol] += testInstanceNum - FalseNegative[nCol] - FalsePositive[nCol] - TruePositive[nCol];
                });

        printIndicators(TruePositive, FalsePositive, TrueNegative, FalseNegative, testInstanceNum);
    }

    private double[] calcAccuracy(int[] TruePositive, int[] TrueNegative, int testInstanceNum){
        double[] accuracy = new double[targetValueNum];
        IntStream.range(0, targetValueNum).forEach(nCol -> {
            accuracy[nCol] = div(add(TruePositive[nCol],  TrueNegative[nCol]), testInstanceNum);
        });

        return accuracy;
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

    private double[] calcGmean(double[] precision, double[] recall){

        double[] Gmean = new double[targetValueNum];
        IntStream.range(0, targetValueNum).forEach(nCol -> {
            Gmean[nCol] = sqrt(mul(precision[nCol], recall[nCol]));
        });

        return Gmean;
    }

    private void printIndicators(int[] TruePositive, int[] FalsePositive, int[] TrueNegative, int[] FalseNegative, int testInstanceNum){
        System.out.println();
        System.out.println(">> Target [ "+targetAttribute.getAttrValueStrByIndex(interesTargetValueInd)+" ]");
        System.out.println("[ Accuracy ] => " + calcAccuracy(TruePositive, TrueNegative, testInstanceNum)[interesTargetValueInd]);
        System.out.println("[ Sensitivity ] => " + calcSensitivity(TruePositive, FalseNegative)[interesTargetValueInd]);
        System.out.println("[ Specificity ] => " + calcSpecificity(TruePositive, FalseNegative)[interesTargetValueInd]);
        System.out.println("[ Precision ] => " + calcPrecision(TruePositive, FalsePositive)[interesTargetValueInd]);
        System.out.println("[ Recall ] => " + calcRecall(TruePositive, FalseNegative)[interesTargetValueInd]);
        System.out.println("[ F1-measure ] => " + calcF1score(calcPrecision(TruePositive, FalsePositive), calcRecall(TruePositive, FalseNegative))[interesTargetValueInd]);
        System.out.println("[ G-mean ] => " + calcGmean(calcPrecision(TruePositive, FalsePositive), calcRecall(TruePositive, FalseNegative))[interesTargetValueInd]);
        System.out.println();
    }

    public int[][] getConfusionMatrix(){
        return confusionMatrix;
    }
}
