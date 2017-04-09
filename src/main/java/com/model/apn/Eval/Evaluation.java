package com.model.apn.Eval;

import com.model.apn.DataStructure.Instances;
import com.model.apn.Model.APN;
import com.model.apn.Preprocess.Filter;
import com.model.apn.Preprocess.MEPA;

import java.util.stream.IntStream;

import static com.model.apn.Config.MAX_FOLDNUM;

/**
 * Created by jack on 2017/3/29.
 */
public class Evaluation {
    Instances instances;

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

            System.out.println(curfoldInd+">>>");
            instances.autoCVInKFold(curfoldInd);
            //model do something

            Instances mepaInstances = Filter.useFilter(instances, new MEPA());

            System.out.println(mepaInstances.getMEPAMembershipEachFold(0, true).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(1, true).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(2, true).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(3, true).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(4, true).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(0, true).size());

            System.out.println(mepaInstances.getMEPAMembershipEachFold(0, false).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(1, false).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(2, false).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(3, false).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(4, false).get(0).getMembershipDegree());
            System.out.println(mepaInstances.getMEPAMembershipEachFold(0, false).size());
        });
        /*
        instances.getInstanceMap().forEach((k,v)->{
            System.out.println("Ind : " + k + " value : "+v.getInstanceMap());
        });
        */

    }

    public void evalTrainTestModel(APN APNmodel, Instances instances, int randseed){

        instances.autoShuffleInstanceOrder();    //Optional, shuffle the instance item
        instances.setRandSeed(randseed);         //Optional
        //model do something

        System.out.println(instances.getMEPAMembershipEachFold(0, true).get(1).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(1, true).get(1).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(2, true).get(1).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(3, true).get(1).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(4, true).get(1).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(0, true).size());

        System.out.println(instances.getMEPAMembershipEachFold(0, false).get(0).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(1, false).get(0).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(2, false).get(0).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(3, false).get(0).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(4, false).get(0).getMembershipDegree());
        System.out.println(instances.getMEPAMembershipEachFold(0, false).size());
        /*
        System.out.println();
        instances.getTestInstanceMap().forEach((k,v)->{
            System.out.println("Ind : " + k+ " value : "+v.getInstanceMap());
        });

        System.out.println();
        instances.getTrainInstanceMap().forEach((k,v)->{
            System.out.println("Ind : " + k + " value : "+v.getInstanceMap());
        });

        System.out.println();
        IntStream.range(0, instances.getAttributeMap().size()).forEach(i->{
            System.out.println("Attr : "+i+" Avg or mode "+instances.getAttributeMap().get(i).getMissingValue());
        });

        System.out.println();
        IntStream.range(0, instances.getAttributeMap().size()).forEach(i->{
            System.out.println("Attr : "+i+" Avg or mode "+instances.getAttributeMap().get(i).getMissingValueTest());
        });
        */
    }

    public void toMatrixString(){

    }
}
