package com.model.apn.Model;

import Container.MEPAMembershipMap;
import DataStructure.Instances;
import com.model.apn.NetworkStructure.APNNetwork;
import com.model.apn.NetworkStructure.APNNetworkStructure;

import java.util.ArrayList;
import java.util.Collections;

import static com.model.apn.Setup.Config.ATTRIBUTE_NUM;
import static com.model.apn.Setup.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Setup.Config.THRESHOLD_NUM;

/**
 * Created by jack on 2017/3/29.
 */
public class APN {
    private Instances instances;
    private APNNetwork APNNet;
    private APNNetworkStructure APNNetStruct;
    private boolean isAPNNetSet = false;

    public APN(){

    }

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    public void setAPNNetworkStructure(boolean isFixed){
        if(this.isAPNNetSet){
            return;
        }

        isAPNNetSet = isFixed;
        this.APNNetStruct = new APNNetworkStructure(instances);
        APNNetStruct.createNetworkStructure();
    }

    public void setAPNNetworkStructureParameters(int t){
        APNNetwork nettmp = new APNNetwork(this.APNNetStruct);

        ArrayList<Double> list = new ArrayList(Collections.nCopies(THRESHOLD_NUM, 0.1));
        list.set(2,0.9);
        list.set(5,0.9);
        list.set(7,0.9);
        nettmp.setParameters(list);

        APNNet = nettmp;
    }

    public void travelAPNmodel(){
        APNNet.travel();
    }

    public void getOutput(){
        APNNet.printPlaceMap();
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
