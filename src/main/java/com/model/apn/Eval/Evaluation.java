package com.model.apn.Eval;

import com.model.apn.Container.MEPAMembership;
import com.model.apn.DataStructure.Instances;
import com.model.apn.Model.APN;
import com.model.apn.Preprocess.Filter;
import com.model.apn.Preprocess.MEPA;

import java.nio.channels.MembershipKey;
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

            System.out.println("[ Fold: "+curfoldInd+" ]");
            instances.autoCVInKFold(curfoldInd);
            Instances mepaInstances = Filter.useFilter(instances, new MEPA());
            //model do something



            for(int i=0;i<mepaInstances.getMEPAMembership(0, true).size();i++){
                MEPAMembership mtmp = mepaInstances.getMEPAMembership(0, true).get(i);
                System.out.println(mtmp.getMembership() + " " + mtmp.getMembershipDegree());
            }

            if(false)
            for(int i=0;i<mepaInstances.getMEPAMembership(0, false).size();i++){
                MEPAMembership mtmp = mepaInstances.getMEPAMembership(0, false).get(i);
                System.out.println(mtmp.getMembership() + " " + mtmp.getMembershipDegree());
            }

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
        Instances mepaInstances = Filter.useFilter(instances, new MEPA());
        //model do something

        for(int i=0;i<mepaInstances.getMEPAMembership(0, true).size();i++){
            MEPAMembership mtmp = mepaInstances.getMEPAMembership(0, true).get(i);
            System.out.println(mtmp.getMembership() + " " + mtmp.getMembershipDegree());
        }

        for(int i=0;i<mepaInstances.getMEPAMembership(0, false).size();i++){
            MEPAMembership mtmp = mepaInstances.getMEPAMembership(0, false).get(i);
            System.out.println(mtmp.getMembership() + " " + mtmp.getMembershipDegree());
        }

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
