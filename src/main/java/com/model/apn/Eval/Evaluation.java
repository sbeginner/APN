package com.model.apn.Eval;

import Container.MEPAMembership;
import Container.MEPAMembershipMap;
import Container.PriorProbabilityAttr;
import DataStructure.Attribute;
import DataStructure.Instances;
import Preprocess.Filter;
import Preprocess.MEPA;
import Setup.Config;
import com.model.apn.Model.APN;

import java.util.stream.IntStream;


import static Setup.Config.ATTRIBUTE_NUM;
import static Setup.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Setup.Config.MAX_FOLDNUM;
import static com.model.apn.Setup.Config.INSTANCE_NUM_TEST;
import static com.model.apn.Setup.Config.PRINT_DETAIL_BTN;

/**
 * Created by jack on 2017/3/29.
 */
public class Evaluation {
    private Instances instances;

    public Evaluation(Instances instances){

    }

    public void crossValidateModel(APN APNmodel, Instances instances, int maxfoldnum, int randseed){

        this.instances = instances;
        instances.setRandSeed(randseed);         //Optional
        instances.autoShuffleInstanceOrder();    //Optional, shuffle the instance item
        instances.setMaxFoldNum(maxfoldnum);

        IntStream.range(0, MAX_FOLDNUM).forEach(curfoldInd -> {

            System.out.println("[ Fold: "+curfoldInd+" ]");
            instances.autoCVInKFold(curfoldInd);
            Instances mepaInstances = Filter.useFilter(instances, new MEPA());
            printInfo(mepaInstances);
            //model do something;
            crossValidateModelAPNProcess(APNmodel, mepaInstances, curfoldInd);

        });
    }

    private void crossValidateModelAPNProcess(APN APNmodel, Instances mepaInstances, int curfoldInd){
        //test(mepaInstances);
        APNmodel.setInstances(mepaInstances);
        APNmodel.setAPNNetworkStructure(true);
        APNmodel.setAPNNetworkStructureParameters();
        //APNmodel.setBionicsAPNnetworkStructure(new ABC());
        APNmodel.travelAPNmodel();
        APNmodel.getOutput();
        APNmodel.test();
    }

    public void evalTrainTestModel(APN APNmodel, Instances instances, int randseed){

        instances.autoShuffleInstanceOrder();    //Optional, shuffle the instance item
        instances.setRandSeed(randseed);         //Optional
        Instances mepaInstances = Filter.useFilter(instances, new MEPA());
        printInfo(mepaInstances);

        //model do something
        evalTrainTestModelAPNProcess(APNmodel, mepaInstances);

    }

    private void evalTrainTestModelAPNProcess(APN APNmodel, Instances mepaInstances){
        APNmodel.setInstances(mepaInstances);
        APNmodel.setAPNNetworkStructure(true);
        APNmodel.setAPNNetworkStructureParameters();
        APNmodel.travelAPNmodel();
        APNmodel.test();
    }

    public void toMatrixString(){

    }

    public void printInfo(Instances instances){
        attributeInfo(instances);
    }

    private void attributeInfo(Instances instances){
        //Attribute Information
        if(!PRINT_DETAIL_BTN){
            return;
        }

        MEPAMembershipMap trainMEPAMembershipMap = instances.getMEPAMembershipMap(false);

        System.out.println();
        System.out.println("<---- Attribute Info (train instance) ---->");
        for(int attrInd=0; attrInd<ATTRIBUTE_NUM; attrInd++){

            System.out.println("---- "+"Attr["+attrInd+"] ["+instances.getAttribute(attrInd).getAttributeName()+"]"+" ----");
            System.out.println("values: "+trainMEPAMembershipMap.getAttributeValue(attrInd));//get attribute value func only for train instance
            System.out.println("0'th Instance, at this attribute, the value is: "+trainMEPAMembershipMap.getAllInstanceByAttr(attrInd).get(0).getMembership());

            if (attrInd == TARGET_ATTRIBUTE) continue;

            Attribute curAttr = instances.getAttribute(attrInd);
            Attribute targetAttr = instances.getAttribute(TARGET_ATTRIBUTE);

            String nInstaceValue= trainMEPAMembershipMap.getAllInstanceByAttr(curAttr).get(0).getMembership(); // n = 0
            String targetInstaceValue= trainMEPAMembershipMap.getAllInstanceByAttr(targetAttr).get(0).getMembership();
            PriorProbabilityAttr pp = trainMEPAMembershipMap.getPriorProbabilityValueByAttr(curAttr);

            System.out.println("Target value: "+trainMEPAMembershipMap.getAttributeValue(targetAttr));

            System.out.println("Prior-probability: "+pp.getProbabilityByAttributeValueMap());

            System.out.print("( "+"attrValue = "+nInstaceValue+", ");
            System.out.print("target = "+targetInstaceValue+") ");
            System.out.println(pp.getProbabilityByAttributeValue(nInstaceValue, targetInstaceValue));
        }
    }
}

