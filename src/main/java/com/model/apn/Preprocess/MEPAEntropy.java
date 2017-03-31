package com.model.apn.Preprocess;

import com.model.apn.Container.MEPAConcernAttr;
import com.model.apn.Container.MEPAConcernAttrList;
import com.model.apn.Math.Arithmetic;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.model.apn.Config.TARGET_ATTRIBUTE;

/**
 * Created by jack on 2017/4/1.
 */
public class MEPAEntropy {



    protected boolean checkChildListExist(ArrayList<MEPAConcernAttr> upperChildList, ArrayList<MEPAConcernAttr> lowerChildList){
        return !upperChildList.isEmpty() && !lowerChildList.isEmpty();
    }

    protected ArrayList<MEPAConcernAttr> upperSplitChildsInstance(ArrayList<MEPAConcernAttr> concernAttrArrayList, double bestThreshold){
        return splitChildsInstance(concernAttrArrayList, bestThreshold, true);
    }

    protected ArrayList<MEPAConcernAttr> lowerSplitChildsInstance(ArrayList<MEPAConcernAttr> concernAttrArrayList, double bestThreshold){
        return splitChildsInstance(concernAttrArrayList, bestThreshold, false);
    }

    protected ArrayList<MEPAConcernAttr> splitChildsInstance(ArrayList<MEPAConcernAttr> concernAttrArrayList, double bestThreshold, boolean isUpper){
        return concernAttrArrayList.stream()
                .filter(item -> item.getConcernAttribute() >= bestThreshold == isUpper)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    protected Map<Double, Map<String, Long>> groupByLevelFunc(ArrayList<MEPAConcernAttr> concernAttrArrayList){
        return concernAttrArrayList.stream()
                .collect(Collectors.groupingBy(MEPAConcernAttr::getConcernAttribute,
                        Collectors.groupingBy(MEPAConcernAttr::getTargetAttributeString, Collectors.counting())));
    }

    protected Map<String, Long> groupByTargetLevelFunc(ArrayList<MEPAConcernAttr> concernAttrArrayList){
        return concernAttrArrayList.stream()
                .collect(Collectors.groupingBy(MEPAConcernAttr::getTargetAttributeString, Collectors.counting()));
    }

    protected double findBestThreshold(Map<String, Long> groupByTargetLevel, Map<Double, Map<String, Long>> groupByLevel, ArrayList<Double> attrValueSorted){

        int bestThresholdInd = IntStream.range(0, attrValueSorted.size())
                .reduce((maxThresholdInd, curThresholdInd) -> {
                    double fatherEntropy = calcFatherEntropy(groupByTargetLevel);
                    double childsEntropyMax = calcSplitChildsEntropy(groupByLevel, attrValueSorted.get(maxThresholdInd));
                    double childsEntropyCur = calcSplitChildsEntropy(groupByLevel, attrValueSorted.get(curThresholdInd));

                    double maxThresholdPerformance = Arithmetic.round(evalInformationGain(fatherEntropy, childsEntropyMax));
                    double curThresholdPerformance = Arithmetic.round(evalInformationGain(fatherEntropy, childsEntropyCur));

                    return maxThresholdPerformance < curThresholdPerformance ? curThresholdInd : maxThresholdInd;
                }).getAsInt();

        //System.out.println(Arithmetic.round(evalInformationGain(calcFatherEntropy(groupByTargetLevel), calcSplitChildsEntropy(groupByLevel, attrValueSorted.get(bestThresholdInd)))));
        return attrValueSorted.get(bestThresholdInd);
    }

    protected double calcFatherEntropy(Map<String, Long> targetAttrValueFrequency){
        return entropyCalculate(new ArrayList(targetAttrValueFrequency.values()));
    }

    protected double evalInformationGain(double fatherEntropy, double childsEntropy){
        return Arithmetic.sub(fatherEntropy, childsEntropy);
    }

    protected double calcSplitChildsEntropy(Map<Double, Map<String, Long>> groupByLevel, double curThreshold){

        Map<String, Long> upperSplitEntropy = upperSplitChildsEntropyFunc(groupByLevel, curThreshold);
        Map<String, Long> lowerSplitEntropy = lowerSplitChildsEntropyFunc(groupByLevel, curThreshold);

        ArrayList<ArrayList<Long>> arrayListforAvg = new ArrayList();
        arrayListforAvg.add(new ArrayList(upperSplitEntropy.values()));
        arrayListforAvg.add(new ArrayList(lowerSplitEntropy.values()));

        return avgEntropyCalculate(arrayListforAvg);
    }

    protected Map<String, Long> upperSplitChildsEntropyFunc(Map<Double, Map<String, Long>> groupByLevel, double curThreshold){
        return splitChildsEntropyFunc(groupByLevel, curThreshold, true);
    }

    protected Map<String, Long> lowerSplitChildsEntropyFunc(Map<Double, Map<String, Long>> groupByLevel, double curThreshold){
        return splitChildsEntropyFunc(groupByLevel, curThreshold, false);
    }

    protected Map<String, Long> splitChildsEntropyFunc(Map<Double, Map<String, Long>> groupByLevel, double curThreshold, boolean isUpper){

        Map<String, Long> splitEntropy = groupByLevel.entrySet()
                .stream()
                .filter(TargetLevelGroupByDigital -> (TargetLevelGroupByDigital.getKey() >= curThreshold) == isUpper)
                .map(TargetLevelMap -> TargetLevelMap.getValue())
                .flatMap(TargetLevelMap -> TargetLevelMap.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.summingLong(frequency -> frequency))));

        return splitEntropy;
    }

    protected double avgEntropyCalculate(ArrayList<ArrayList<Long>> arrayListforAvg){
        double allSum = arrayListforAvg.stream()
                .mapToDouble(item -> sumCalculate(item))
                .sum();

        double avgEntropy = arrayListforAvg.stream().mapToDouble(arrayItem -> {
            double unitWeight = Arithmetic.div(sumCalculate(arrayItem), allSum);
            double unitEntropy = entropyCalculate(arrayItem);
            return Arithmetic.mul(unitWeight, unitEntropy);
        }).sum();

        return Arithmetic.round(avgEntropy);
    }

    protected double entropyCalculate(ArrayList<Long> targetAttrValueFrequency){
        //Process target attribute has multi attribute-value (multi-class)
        double totalFrequency = sumCalculate(targetAttrValueFrequency);
        double totalEntropy = targetAttrValueFrequency.stream().mapToDouble(frequency -> {
            double divFreqInFreqtotal = Arithmetic.div(frequency, totalFrequency);
            double nonsignUnitEntropy = Arithmetic.mul(divFreqInFreqtotal, Arithmetic.ln(divFreqInFreqtotal));
            double unitEntropy = Arithmetic.mul(-1, nonsignUnitEntropy);

            return unitEntropy;
        }).sum();

        return Arithmetic.round(totalEntropy);
    }

    protected double sumCalculate(ArrayList<Long> targetAttrValueFrequency){
        return Arithmetic.round(targetAttrValueFrequency.stream()
                .mapToDouble(Long::doubleValue)
                .sum());
    }
}
