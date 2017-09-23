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

        /* For AUC*/
        //prepareAUC(APNOutputInstanceInfoList);
        //APNOutputInstanceInfoList.get(0).getRealTargetValue()
        //APNOutputInstanceInfoList.get(0).getAPNPredict()

        APNOutputInstanceInfoList.forEach(outputInstanceInfo -> {
            int predictInd = targetAttribute.getAttrValueIndByString(outputInstanceInfo.getAPNPredict());
            int realInd = targetAttribute.getAttrValueIndByString(outputInstanceInfo.getRealTargetValue());
            confusionMatrix[predictInd][realInd] += 1;
        });

        this.confusionMatrix = confusionMatrix;

        printConfusionMatrixInfo(confusionMatrix);
    }


    private void prepareAUC(ArrayList<APNOutputInstanceInfo> APNOutputInstanceInfoList){
        List<APNOutputInstanceInfo> sortedList = APNOutputInstanceInfoList.stream()
                .sorted(Comparator.comparing(APNOutputInstanceInfo::getAPNPredictDegreeNormalize).reversed())
                .collect(Collectors.toList());

        List<Double> sortedProbabilityList= sortedList.stream()
                .mapToDouble(APNOutputInstanceInfo::getAPNPredictDegreeNormalize)
                .boxed()
                .collect(Collectors.toList());

        List<Double> AUCRankList = calcAUCRank(sortedProbabilityList);
        int cur = 0;

        List<Boolean> sortedTrueFalseList = new ArrayList<>();
        sortedList.stream().forEach(outputInstanceInfo->{
            int realInd = targetAttribute.getAttrValueIndByString(outputInstanceInfo.getRealTargetValue());
            if(realInd == cur){
                sortedTrueFalseList.add(true);
            }else {
                sortedTrueFalseList.add(false);
            }
        });

        int True = (int) sortedTrueFalseList.stream().filter(Boolean::booleanValue).count();
        int False = sortedTrueFalseList.size() - True;
        double TrueSum = IntStream.range(0, sortedTrueFalseList.size()).filter(i->sortedTrueFalseList.get(i)).mapToDouble(i->AUCRankList.get(i)).sum();

        System.out.println(AUCRankList);
        System.out.println(TrueSum - div(True*(True+1), 2));
        System.out.println(True * False);

        if(True*False != 0){
            System.out.println(div(TrueSum - div(True*(True+1), 2), True * False));
        }
    }

    private List<Double> calcAUCRank(List<Double> sortedList){

        int rank = sortedList.size()+1;
        List<Double> rankList = new ArrayList(sortedList.size());

        for(int i = 0;i<sortedList.size();i++){

            int cnt = 0;
            for(int j = i+1;j<sortedList.size();j++){
                if(sortedList.get(i).doubleValue()==sortedList.get(j).doubleValue()){
                    cnt++;
                    continue;
                }

                break;
            }

            rank--;

            if(cnt == 0){
                rankList.add((double)rank);
            }else {
                int start = cnt;
                while (start >= 0){
                    rankList.add(div((rank + rank - cnt)*cnt, 2*cnt));
                    start--;
                    i++;
                }
                i--;
                rank -= cnt;
            }
        }

        return rankList;
    }



    public void printConfusionMatrixInfo(){
        int[][] confusionMatrix = this.confusionMatrix;
        printConfusionMatrixInfo(confusionMatrix);
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
        System.out.println("[ Accuracy ] => " + calcAccuracy(TruePositive, testInstanceNum));
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
