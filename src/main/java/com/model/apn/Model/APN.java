package com.model.apn.Model;

import Container.MEPAMembershipMap;
import DataStructure.Instances;
import com.model.apn.NetworkStructure.APNNetworkStructure;

import static Setup.Config.ATTRIBUTE_NUM;
import static Setup.Config.TARGET_ATTRIBUTE;

/**
 * Created by jack on 2017/3/29.
 */
public class APN {
    Instances instances;
    public APN(){

    }

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    public void setAPNnetworkStructure(){
        System.out.println(instances.getAttribute(0).getAttributeName());
        APNNetworkStructure APNnet = new APNNetworkStructure(instances);
        APNnet.testa();
    }

    public void travelAPNmodel(){

    }

    public void getOutput(){

    }

    public void test(){

    }

    private void test1(){


        /*
        MEPAMembershipMap trainMEPAMembershipMap = instances.getMEPAMembershipMap(false);
        for(int i=0;i<ATTRIBUTE_NUM;i++){
            System.out.println(instances.getAttribute(i).getThresholdList()+" "+instances.getAttribute(i).getIndex()+" "+instances.getAttributeMap().size());
            for(int j = 0;j<5;j++){
                MEPAMembership mtmp = trainMEPAMembershipMap.getAllInstanceByAttr(i).get(j);
                System.out.print(instances.getTrainInstance(j).getInstanceValue(i)+" "+mtmp.getMembership()+" "+mtmp.getMembershipDegree());
                System.out.println();
            }
        }
        */

        MEPAMembershipMap trainMEPAMembershipMap = instances.getMEPAMembershipMap(false);

        for(int i=0;i<ATTRIBUTE_NUM;i++){

            System.out.println(" => ");
            System.out.println("Attr "+i+" ["+instances.getAttribute(i).getAttributeName()+"] values: "+trainMEPAMembershipMap.getAttributeValue(i));
            System.out.println("instance 0, Attr "+i+" "+trainMEPAMembershipMap.getAllInstanceByAttr(i).get(0).getMembership());
            System.out.println("Attr "+i+" ,name: "+instances.getAttribute(i).getAttributeName().toString());

            if (i == TARGET_ATTRIBUTE) continue;

            System.out.println("Attr "+i+" prior-prob => "+trainMEPAMembershipMap.getPriorProbabilityMap().get(instances.getAttribute(i)).getProbabilityByAttributeValueMap());
            System.out.println("Attr"+i+" value "+trainMEPAMembershipMap.getAllInstanceByAttr(i).get(0).getMembership()+", Iris-virginica"+" "
                    +trainMEPAMembershipMap.getPriorProbabilityValueByAttr(i).getProbabilityByAttributeValue(
                    trainMEPAMembershipMap.getAllInstanceByAttr(i).get(0).getMembership(),"Iris-virginica"));

            System.out.println();
            trainMEPAMembershipMap.getPriorProbabilityValueByAttr(i).getProbabilityByAttributeValue(0,0);
            System.out.println();
        }

    }

}
