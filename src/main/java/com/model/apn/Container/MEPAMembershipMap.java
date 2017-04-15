package com.model.apn.Container;

import com.model.apn.DataStructure.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.model.apn.Config.ATTRIBUTEVALUE_NUM;
import static com.model.apn.Config.INSTANCE_NUM;

/**
 * Created by jack on 2017/4/2.
 */
public class MEPAMembershipMap {
    private HashMap<Attribute, ArrayList<MEPAMembership>> MEPAMembershipMap;
    private HashMap<Attribute, ArrayList<String>> attributeValueMap;
    HashMap<Attribute, PriorProbabilityAttr> priorProbabilityMap;
    boolean isTargetAttribute = false;

    public MEPAMembershipMap(){
        MEPAMembershipMap = new HashMap(INSTANCE_NUM);
        attributeValueMap = new HashMap(ATTRIBUTEVALUE_NUM);
        priorProbabilityMap = new HashMap();
    }

    public void put(Attribute curAttr, ArrayList<MEPAMembership> member){
        MEPAMembershipMap.put(curAttr, member);
    }

    public void setAttributeValue(Attribute curAttr, ArrayList<String> attributeValueList){
        attributeValueMap.put(curAttr, attributeValueList);
    }

    public void setTargetValue(Attribute curAttr, ArrayList<StringBuilder> targetValueList){
        if(isTargetAttribute == true){
            return;
        }

        isTargetAttribute = true;
        ArrayList<String> targetListtmp = targetValueList.stream()
                .map(item -> item.toString())
                .collect(Collectors.toCollection(ArrayList::new));

        setAttributeValue(curAttr, targetListtmp);
    }

    public void setPriorProbabilityMap(Attribute curAttribute, PriorProbabilityAttr priorProbabilityAttr){
        priorProbabilityMap.put(curAttribute, priorProbabilityAttr);
    }

    public ArrayList<MEPAMembership> get(Attribute curAttr){
        return MEPAMembershipMap.get(curAttr);
    }

    public ArrayList<String> getAttributeValue(Attribute curAttr){
        return attributeValueMap.get(curAttr);
    }

    public HashMap<Attribute, PriorProbabilityAttr> getPriorProbabilityMap(){
        return priorProbabilityMap;
    }
}
