package com.model.apn.Eval;

import Container.MEPAMembershipMap;
import Container.PriorProbabilityAttr;
import DataStructure.Attribute;
import DataStructure.Instances;
import Preprocess.Filter;
import Preprocess.MEPA;
import com.model.apn.Model.APN;
import com.model.apn.BionicsMethod.Bionics;

import java.util.Objects;
import java.util.stream.IntStream;


import static Setup.Config.ATTRIBUTE_NUM;
import static Setup.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Setup.Config.MAX_FOLDNUM;
import static com.model.apn.Setup.Config.PRINT_DETAIL_BTN;

/**
 * Created by jack on 2017/3/29.
 */
public class Evaluation {
    private Instances instances;

    public Evaluation(Instances instances){
        this.instances = instances;
    }

    /*
    * Cross validation
    * */
    public void crossValidateModel(APN APNmodel, int maxfoldnum){
        crossValidateModel(APNmodel, maxfoldnum, null);
    }

    public void crossValidateModel(APN APNmodel, int maxfoldnum, Bionics bionics){

        instances.autoShuffleInstanceOrder();    //Optional, shuffle the instance item
        instances.setMaxFoldNum(maxfoldnum);

        IntStream.range(0, MAX_FOLDNUM).forEach(curfoldInd -> {

            instances.autoCVInKFold(curfoldInd);
            Instances mepaInstances = Filter.useFilter(instances, new MEPA());

            System.out.println("[ Fold: "+curfoldInd+" ]");
            printInfo(mepaInstances);

            //model do something;
            evalAPNProcess(APNmodel, mepaInstances, curfoldInd, bionics);

        });

        //result
        toTotalMatrixString(APNmodel);
    }

    /*
    * Train & Test
    * */
    public void evalTrainTestModel(APN APNmodel){
        evalTrainTestModel(APNmodel, null);
    }

    public void evalTrainTestModel(APN APNmodel, Bionics bionics){

        instances.autoShuffleInstanceOrder();    //Optional, shuffle the instance item
        Instances mepaInstances = Filter.useFilter(instances, new MEPA());

        printInfo(mepaInstances);

        //model do something
        evalAPNProcess(APNmodel, mepaInstances, 0, bionics);

        //result
        toTotalMatrixString(APNmodel);
    }

    /*
    * General APN process
    * */
    private void evalAPNProcess(APN APNmodel, Instances mepaInstances, int curfoldInd, Bionics bionics){
        APNmodel.setInstances(mepaInstances);
        APNmodel.setAPNNetworkStructure(true);
        APNmodel.setAPNNetworkStructureParameters();

        if(!Objects.isNull(bionics)){
            APNmodel.setBionicsAPNnetworkStructure(curfoldInd, bionics);
        }

        APNmodel.travelAPNmodel(curfoldInd);

        toEachMatrixString(APNmodel);
    }

    /*
    * Print something...
    * */
    private void printInfo(Instances instances){
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

    private void toTotalMatrixString(APN APNmodel){
        APNmodel.printNetworkStructure();
        System.out.println();
        System.out.println("|- - - - - [ Total Result ] - - - - -|");
        APNmodel.getAPNOutputInfo().getTotalConfusionMatrixOutput();
    }

    private void toEachMatrixString(APN APNmodel){
        System.out.println();
        System.out.println("|- - - - - [ Each Result ] - - - - -|");
        APNmodel.getAPNOutputInfo().getEachConfusionMatrixOutput();
    }
}

