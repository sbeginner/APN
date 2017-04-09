package com.model.apn.Container;

import java.util.ArrayList;

/**
 * Created by jack on 2017/3/31.
 */
public class MEPAConcernAttrList {
    //MEPA concentrates to the two attributes in the current time
    //Current attribute (one of the attribute in the attribute set) and the target attribute
    ArrayList<MEPAConcernAttr> concernAttrList;

    public MEPAConcernAttrList(){
        concernAttrList = new ArrayList();
    }

    public void addMEPAConcernAttr(double concernAttribute, StringBuilder targetAttribute){
        concernAttrList.add(new MEPAConcernAttr(concernAttribute, targetAttribute));
    }

    public ArrayList<MEPAConcernAttr> getConcernAttrList(){
        return this.concernAttrList;
    }
}
