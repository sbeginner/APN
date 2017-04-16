package com.model.apn.Model;

import com.model.apn.Container.MEPAMembershipMap;
import com.model.apn.DataStructure.Instances;

import static com.model.apn.Config.ATTRIBUTE_NUM;
import static com.model.apn.Config.TARGET_ATTRIBUTE;

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

    }

    public void travelAPNmodel(){

    }

    public void getOutput(){

    }

    public void test(){

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

        }

    }
}
