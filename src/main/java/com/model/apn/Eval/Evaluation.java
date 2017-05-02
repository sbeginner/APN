package com.model.apn.Eval;

import com.model.apn.Container.MEPAMembership;
import com.model.apn.Container.MEPAMembershipMap;
import com.model.apn.DataStructure.Instances;
import com.model.apn.Model.APN;
import com.model.apn.Preprocess.Filter;
import com.model.apn.Preprocess.MEPA;

import java.nio.channels.MembershipKey;
import java.util.stream.IntStream;

import static com.model.apn.Config.*;

/**
 * Created by jack on 2017/3/29.
 */
public class Evaluation {
    private Instances instances;

    public Evaluation(Instances instances){
        instances.getAttributeMap().forEach((k,v)->{
            System.out.println("Attr : " + k + " isString : " +v.getAttributeType()+ " Value : "+v.getAllValue());
        });
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
            //model do something;
            System.out.println(mepaInstances.getAttribute(0).getThresholdList());
            System.out.println(mepaInstances.getAttribute(1).getThresholdList());
            System.out.println(mepaInstances.getAttribute(2).getThresholdList());
            System.out.println(mepaInstances.getAttribute(3).getThresholdList());

            //test(mepaInstances);

            APNmodel.setInstances(mepaInstances);
            APNmodel.setAPNnetworkStructure();
            //APNmodel.setBionicsAPNnetworkStructure(new ABC());
            APNmodel.travelAPNmodel();
            APNmodel.getOutput();
            APNmodel.test();

        });

    }

    public void evalTrainTestModel(APN APNmodel, Instances instances, int randseed){

        instances.autoShuffleInstanceOrder();    //Optional, shuffle the instance item
        instances.setRandSeed(randseed);         //Optional
        Instances mepaInstances = Filter.useFilter(instances, new MEPA());
        //model do something

        test(mepaInstances);
        APNmodel.setInstances(mepaInstances);
        APNmodel.test();
    }

    public void toMatrixString(){

    }

    private void test(Instances mepaInstances){
        if(true)
        return;

        System.out.println(mepaInstances.getMEPAMembershipMap(false).getAttributeValue(0));
        System.out.println(mepaInstances.getMEPAMembershipMap(false).getAllInstanceByAttr(0).get(0).getMembership());
        System.out.println(mepaInstances.getMEPAMembershipMap(false).getPriorProbabilityValueByAttr(0).getProbabilityByAttributeValue(
                mepaInstances.getMEPAMembershipMap(false).getAllInstanceByAttr(0).get(0).getMembership(),"Iris-virginica"));

        for(int i=0;i<INSTANCE_NUM_TEST - 1;i++){
            MEPAMembership mtmp = mepaInstances.getMEPAMembership(0, true).get(i);
            System.out.println(mtmp.getMembership() + " " + mtmp.getMembershipDegree());
        }

        if(false)
            for(int i=0;i<mepaInstances.getMEPAMembership(0, false).size();i++){
                MEPAMembership mtmp = mepaInstances.getMEPAMembership(0, false).get(i);
                System.out.println(mtmp.getMembership() + " " + mtmp.getMembershipDegree());
            }
    }
}
