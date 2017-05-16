package com.model.apn.Container;

import Container.MEPAMembershipMap;
import Container.PriorProbabilityAttr;
import DataStructure.Instances;
import com.model.apn.APNObject.Place;

import java.util.*;
import java.util.stream.Collectors;

import static MathCalculate.Arithmetic.*;
import static Setup.Config.*;
import static com.model.apn.Setup.Config.PROBABILITY_PREDICT_BTN;
import static com.model.apn.Setup.Config.ROOT_PLACE;

/**
 * Created by JACK on 2017/5/13.
 */
public class APNOutputInstanceInfo {
    private Instances instances;
    private int instanceInd = NONVALUE_INTEGER;
    private HashMap<String, Double> APNPredictTargetDegreeMap;    //The predict degree in each target value
    private String APNPredict;    //APN predict target value
    private double APNPredictDegreeNormalize;    // APNPredictDegreeNormalize = APNPredictDegree/totalAPNPredictDegree
    private double APNPredictDegree;    //Only the APN selected target predict degree
    private double totalAPNPredictDegree;    //All the target predict degree
    private boolean IsAPNPredictSameDegree = false;
    private String APNProbabilityPredict;   //When APN predict the same value, we can use this to get the most probability target value(answer)
    private String RealTargetValue;

    private double MSE;    //Mean Squared Error

    public APNOutputInstanceInfo(int instanceInd, Instances instances){
        this.instanceInd = instanceInd;
        this.instances = instances;
    }

    public void create(HashMap<Integer, Place> rootPlaceMap, HashMap<Integer, Place> placeMap){

        setAPNPredictTargetDegreeMap(rootPlaceMap);
        setAPNPredict(rootPlaceMap);

        setIsAPNPredictSameDegree(rootPlaceMap, getAPNPredictDegree());
        if(IsAPNPredictSameDegree) setAPNProbabilityPredict(rootPlaceMap, placeMap, getAPNPredictDegree());

        setCurrentInstanceRealTarget(true);
        setMSE(rootPlaceMap);
    }

    public double getMeanSquaredErrorForBio(HashMap<Integer, Place> rootPlaceMap){

        setCurrentInstanceRealTarget(false);
        setMSE(rootPlaceMap);

        return getMeanSquaredError();
    }

    private  HashMap<String, Double> calcTargetPriorProbability(HashMap<Integer, Place> placeMap){
        //While the APN model output relationship degree can't directly predict (classify)
        ArrayList<String> attributeValueList = instances.getMEPAMembershipMap(false).getAttributeValue(TARGET_ATTRIBUTE);
        Iterator iterator = attributeValueList.iterator();
        HashMap<String, Double> targetPriorProbabilityMap = new HashMap(ATTRIBUTEVALUE_NUM);

        while (iterator.hasNext()){
            String curTargetValue = iterator.next().toString();

            //Find the highest prior-probability target value,ex: "class0" 0.12, "class1" 0.58,..., "classn" 0.77
            double targetPriorProbability = placeMap.values()
                    .stream()
                    .filter(place -> place.getTypeValue() != ROOT_PLACE)
                    .mapToDouble(place -> {
                        MEPAMembershipMap trainMEPAMembershipMap  = instances.getMEPAMembershipMap(false);
                        PriorProbabilityAttr ppAttr = trainMEPAMembershipMap.getPriorProbabilityValueByAttr(place.getAttribute());
                        return ppAttr.getProbabilityByAttributeValue(place.getTestAttributeValue(), curTargetValue);
                    })
                    .reduce(1, (multiplyTotalNum, curNum) -> mul(multiplyTotalNum, curNum));

            targetPriorProbabilityMap.put(curTargetValue, targetPriorProbability);
        }

        return targetPriorProbabilityMap;
    }

    private String getProbabilityPredictTarget(HashMap<Integer, Place> rootPlaceMap, HashMap<Integer, Place> placeMap, double maxAPNRelationshipDegree){
        HashMap<String, Double> targetPriorProbabilityMap = calcTargetPriorProbability(placeMap);

        List<Place> pList = rootPlaceMap.values()
                .stream()
                .filter(place -> place.getRelationshipDegree()== maxAPNRelationshipDegree)
                .collect(Collectors.toList());

        Place maxPlace = pList.stream()
                .max(Comparator.comparing(place -> targetPriorProbabilityMap.get(place.getTestAttributeValue())))
                .orElse(null);

        return maxPlace.getTestAttributeValue();
    }

    private boolean checkAPNOutputMultipleSameDegreeIsExist(HashMap<Integer, Place> placeMap, double maxAPNRelationshipDegree){
        boolean MultipleSameDegree = placeMap.values()
                .stream()
                .filter(place -> place.getRelationshipDegree() == maxAPNRelationshipDegree)
                .count() > 1;
        return MultipleSameDegree;
    }

    private Place getAPNMaxRelationshipTarget(HashMap<Integer, Place> placeMap){
        Place maxPlace = placeMap.values()
                .stream()
                .max(Comparator.comparing(Place::getRelationshipDegree))
                .orElse(null);
        return maxPlace;
    }

    private String getCurrentInstanceRealTarget(int testInstanceInd, boolean isTest){
        if(isTest){
            String curTestInstanceIndTarget = instances.getMEPAMembershipMap(true)
                    .getAllInstanceByAttr(TARGET_ATTRIBUTE)
                    .get(testInstanceInd)
                    .getMembership();

            return curTestInstanceIndTarget;
        }

        String curTestInstanceIndTarget = instances.getMEPAMembershipMap(false)
                .getAllInstanceByAttr(TARGET_ATTRIBUTE)
                .get(testInstanceInd)
                .getMembership();

        return curTestInstanceIndTarget;
    }

    private double calcMeanSquaredError(HashMap<Integer, Place> placeMap, String currentInstanceRealTarget){
        double MSE = placeMap.values()
                .stream()
                .mapToDouble(place -> {
                    if(currentInstanceRealTarget.equals(place.getTestAttributeValue())){
                        return mul(sub(1, place.getRelationshipDegree()), sub(1, place.getRelationshipDegree()));
                    }else {
                        return mul(sub(0, place.getRelationshipDegree()), sub(0, place.getRelationshipDegree()));
                    }
                }).sum();

        return round(MSE);
    }


    private void setAPNPredictTargetDegreeMap(HashMap<Integer, Place> rootPlaceMap){
        Map<String, Double> APNPredictTargetDegreeMaptmp = rootPlaceMap
                .values()
                .stream()
                .collect(Collectors.toMap(Place::getTestAttributeValue, Place::getRelationshipDegree));

        this.APNPredictTargetDegreeMap = new HashMap(APNPredictTargetDegreeMaptmp);
    }

    private double setTotalAPNPredictDegree(HashMap<Integer, Place> rootPlaceMap){
        return rootPlaceMap
                .values()
                .stream()
                .mapToDouble(Place::getRelationshipDegree).sum();
    }

    private void setAPNPredict(HashMap<Integer, Place> rootPlaceMap){
        Place maxPlace = getAPNMaxRelationshipTarget(rootPlaceMap);

        this.APNPredict = maxPlace.getTestAttributeValue();
        this.APNPredictDegree = maxPlace.getRelationshipDegree();
        this.totalAPNPredictDegree = setTotalAPNPredictDegree(rootPlaceMap);
        this.APNPredictDegreeNormalize = div(APNPredictDegree, totalAPNPredictDegree);
        this.APNPredictDegreeNormalize = div(APNPredictDegree, totalAPNPredictDegree);
    }

    private void setAPNProbabilityPredict(HashMap<Integer, Place> rootPlaceMap, HashMap<Integer, Place> placeMap, double maxAPNRelationshipDegree){
        this.APNProbabilityPredict = getProbabilityPredictTarget(rootPlaceMap, placeMap, maxAPNRelationshipDegree);
    }

    private void setIsAPNPredictSameDegree(HashMap<Integer, Place> rootPlaceMap, double maxAPNRelationshipDegree){
        this.IsAPNPredictSameDegree = checkAPNOutputMultipleSameDegreeIsExist(rootPlaceMap, maxAPNRelationshipDegree);
    }

    private void setCurrentInstanceRealTarget(boolean isTest){
        if(isTest){
            this.RealTargetValue = getCurrentInstanceRealTarget(this.instanceInd, true);
            return;
        }

        this.RealTargetValue = getCurrentInstanceRealTarget(this.instanceInd, false);
    }

    private void setMSE(HashMap<Integer, Place> rootPlaceMap){
        this.MSE = calcMeanSquaredError(rootPlaceMap, this.RealTargetValue);
    }

    public double getAPNPredictDegree(){
        return this.APNPredictDegree;
    }

    public double getAPNPredictDegreeNormalize(){
        if(Double.isNaN(APNPredictDegreeNormalize)){
            return MIN_DOUBLENUM;
        }

        return APNPredictDegreeNormalize;
    }

    public String getAPNPredict(){
        if(this.IsAPNPredictSameDegree && PROBABILITY_PREDICT_BTN){
            return this.APNProbabilityPredict;
        }
        return this.APNPredict;
    }

    public String getRealTargetValue(){
        return RealTargetValue;
    }

    private double getMeanSquaredError(){
        return MSE;
    }




    public void print(){
        System.out.println();
        System.out.println(getAPNPredict()+" "+getRealTargetValue()+" "+ getAPNPredictDegreeNormalize());
        System.out.println(instances.getTestInstanceMap().get(instanceInd).getInstanceItemMap());
        System.out.println();
    }



}
