package com.model.apn.DataStructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * Created by jack on 2017/3/20.
 */
public class Attribute {

    private int index;
    StringBuilder attributeName;
    ArrayList<StringBuilder> attrvalueList;

    HashSet<String> attrvalueSet;

    public Attribute(StringBuilder attributeName){
        //set attribute index and name
        this.attributeName = attributeName;
        attrvalueSet = new HashSet();
        attrvalueList = new  ArrayList();
    }

    public void setIndex(int index){
        //set attribute index
        this.index = index;
    }

    public void setValue(ArrayList<StringBuilder> attrvalueList){
        //set 'attribute value' list
        this.attrvalueList = attrvalueList;
    }

    public void setHashSetValue(String attrvalue){
        //set 'attribute value' list
        String ostr = Optional.ofNullable(attrvalue).filter(s -> !s.isEmpty())
                .orElse("-unknown-");

        this.attrvalueSet.add(ostr);
        //System.out.println(attrvalueSet.size());
    }

    public StringBuilder getAttributeName(){
        //get attribute name
        return attributeName;
    }

    public int getIndex(){
        //set attribute index
        return index;
    }

    public StringBuilder getValue(int index){
        //get 'attribute value'
        return attrvalueList.get(index);
    }

    public ArrayList<StringBuilder> getAllValue(){
        //get 'attribute value'
        return attrvalueList;
    }

    public OptionalInt getIndexOfValue(String value){
        //get 'attribute value' index
        return IntStream.range(0, attrvalueList.size()).filter(i -> value.equals(attrvalueList.get(i))).findFirst();
    }

    public void  transAttrValueSet2AttrValueList(){
        attrvalueSet.forEach(tmp -> attrvalueList.add(new StringBuilder(tmp)));
    }

}
