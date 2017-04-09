package com.model.apn.Container;

import com.model.apn.DataStructure.Attribute;

import java.util.ArrayList;
import java.util.HashMap;

import static com.model.apn.Config.INSTANCE_NUM;

/**
 * Created by jack on 2017/4/2.
 */
public class MEPAMembershipMap {
    private HashMap<Attribute, ArrayList<MEPAMembership>> MEPAMembershipEachFold;

    public MEPAMembershipMap(){
        MEPAMembershipEachFold = new HashMap(INSTANCE_NUM);
    }

    public void put(Attribute curAttr, ArrayList<MEPAMembership> member){
        MEPAMembershipEachFold.put(curAttr, member);
    }

    public ArrayList<MEPAMembership> get(Attribute curAttr){
        return MEPAMembershipEachFold.get(curAttr);
    }

}
