package com.model.apn.DataStructure;

import java.util.*;
import java.util.stream.IntStream;

import static com.model.apn.Config.UNKNOWNVALUE;

/**
 * Created by jack on 2017/3/20.
 */
public class Attribute {

    private int index;
    StringBuilder attributeName;
    Boolean attributeTypeisStr;
    ArrayList<StringBuilder> attrvalueList;

    HashMap<String, Integer> attrvalueMap;

    public Attribute(StringBuilder attributeName){
        //set attribute index and name
        this.attributeName = attributeName;
        attrvalueMap = new HashMap();
        attrvalueList = new  ArrayList();
    }

    public void setIndex(int index){
        //set attribute index
        this.index = index;
    }

    public void setAttributeType(boolean attributeTypeisStr){
        //set attribute index
        this.attributeTypeisStr = attributeTypeisStr;
    }

    public void setValue(ArrayList<StringBuilder> attrvalueList){
        //set 'attribute value' list
        this.attrvalueList = attrvalueList;
    }

    public void setHashMapValue(String attrvalue){
        //set 'attribute value' list
        if(UNKNOWNVALUE.equals(attrvalue)){
            return;
        }

        this.attrvalueMap.put(attrvalue, autoItemFrequencyCounter(this.attrvalueMap.get(attrvalue)));
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

    public boolean getAttributeType(){
        //set attribute index
        return attributeTypeisStr;
    }

    public int getAllValuesize(){
        //set attribute size
        return attrvalueList.size();
    }

    public OptionalInt getIndexOfValue(String value){
        //get 'attribute value' index
        return IntStream.range(0, attrvalueList.size()).filter(i -> value.equals(attrvalueList.get(i))).findFirst();
    }

    public HashMap<String, Integer>  getAttrValueMap(){
        return attrvalueMap;
    }

    public void  transAttrValueSet2AttrValueList(){
        attrvalueMap.forEach((attrValue, attrValuefrequency)->{
            attrvalueList.add(new StringBuilder(attrValue));
        });
        /*
        attrvalueMap.forEach((attrValue, attrValuefrequency)->{
            System.out.println(attrValue+" "+attrValuefrequency);
        });
        */
    }

    private int autoItemFrequencyCounter(Integer itemfrequency){
        itemfrequency = Optional.ofNullable(itemfrequency).orElse(new Integer(0)) + 1;
        return itemfrequency;
    }
}
