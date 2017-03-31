package com.model.apn.DataStructure;

import com.model.apn.Math.Arithmetic;

import java.util.*;
import java.util.stream.IntStream;

import static com.model.apn.Config.ATTRIBUTEVALUE_NUM;
import static com.model.apn.Config.UNKNOWNVALUE;

/**
 * Created by jack on 2017/3/20.
 */
public class Attribute {

    private int index;                                       //Attribute index
    private Boolean attributeTypeisStr;                      //Attribute type, string as true, digital as false
    private StringBuilder attributeName;                     //Attribute name
    private ArrayList<StringBuilder> attrvalueStringList;    //Attribute value list for all value (includes string and digital)
    private ArrayList<Double> attrvalueDigitList;            //Attribute value list for only digital type
    private HashMap<String, Integer> attrvalueMap;           //Attribute value set in train or k-fold validation mode
    private HashMap<String, Integer> attrvalueMapforTest;    //Attribute value set in test mode

    private double formissingNum;              //For replacing The missing number in train or k-fold validation mode
    private String formissingStr;              //For replacing The missing string in train or k-fold validation mode
    private double formissingNumForTest;       //For replacing The missing number in test mode
    private String formissingStrForTest;       //For replacing The missing string in test mode


    public Attribute(StringBuilder attributeName){
        //Set attribute index and name
        this.attributeName = attributeName;
        attrvalueStringList = new  ArrayList(ATTRIBUTEVALUE_NUM);
        attrvalueDigitList = new  ArrayList(ATTRIBUTEVALUE_NUM);
        attrvalueMap = new HashMap(ATTRIBUTEVALUE_NUM);
        attrvalueMapforTest = new HashMap(ATTRIBUTEVALUE_NUM);
    }

    public void setIndex(int index){
        //Set attribute index
        this.index = index;
    }

    public void setAttributeType(boolean attributeTypeisStr){
        //Set attribute index
        this.attributeTypeisStr = attributeTypeisStr;
    }

    public void setformissingValue(double formissingNum, boolean checkIsTest){
        //Set missing digital value
        if(checkIsTest){
            this.formissingNumForTest = formissingNum;
            return;
        }
        this.formissingNum = formissingNum;
    }

    public void setformissingValue(String formissingStr, boolean checkIsTest){
        //Set missing string value
        if(checkIsTest){
            this.formissingStrForTest = formissingStr;
            return;
        }
        this.formissingStr = formissingStr;
    }

    public void setHashMapValue(String attrvalue,boolean checkIsTest){
        //Set attribute value list
        if(UNKNOWNVALUE.equals(attrvalue)){
            return;
        }

        if(checkIsTest){
            this.attrvalueMapforTest.put(attrvalue, autoItemFrequencyCounter(this.attrvalueMapforTest.get(attrvalue)));
            return;
        }

        this.attrvalueMap.put(attrvalue, autoItemFrequencyCounter(this.attrvalueMap.get(attrvalue)));
    }


    public StringBuilder getAttributeName(){
        //Get attribute name
        return attributeName;
    }

    public StringBuilder getValue(int index){
        //Get 'attribute value' by index
        return attrvalueStringList.get(index);
    }

    public int getIndex(){
        //Get attribute index
        return index;
    }

    public int getAllValueSize(){
        //Get attribute size
        return attrvalueStringList.size();
    }

    public int getIndexOfValue(String value){
        //get 'attribute value' index, error return -1
        return IntStream.range(0, attrvalueStringList.size()).filter(i -> value.equals(attrvalueStringList.get(i))).findFirst().orElse(-1);
    }

    public boolean getAttributeType(){
        //Get attribute type
        return attributeTypeisStr;
    }

    public String getMissingValue(){
        //Get value to replace the original missing value
        if(attributeTypeisStr){
            return this.formissingStr;
        }
        return String.valueOf(this.formissingNum);
    }

    public String getMissingValueTest(){
        //Get value to replace the original missing value
        if(attributeTypeisStr){
            return this.formissingStrForTest;
        }
        return String.valueOf(this.formissingNumForTest);
    }

    public ArrayList<StringBuilder> getAllValue(){
        //Get 'attribute value' list
        return attrvalueStringList;
    }

    public HashMap<String, Integer>  getAttrValueMap(boolean checkIsTest){
        //Get attribute value map
        if(checkIsTest){
            return attrvalueMapforTest;
        }
        return attrvalueMap;
    }


    public void  transAttrValueSet2AttrValueList(){
        //transfer the hash set to arraylist
        attrvalueMap.forEach((attrValue, attrValuefrequency)->{
            attrvalueStringList.add(new StringBuilder(attrValue));
        });
    }

    public void createDoubleList(boolean isDigitalType){
        if(isDigitalType){
            attrvalueMap.forEach((attrValue, attrValuefrequency)->{
                attrvalueDigitList.add(Arithmetic.createDouble(attrValue));
            });
        }
    }


    public ArrayList<Double> getAllValueInDigital(){
        //Get 'attribute value' digital list
        return attrvalueDigitList;
    }
    private int autoItemFrequencyCounter(Integer itemfrequency){
        //Auto calculate the attribute value frequency
        itemfrequency = Optional.ofNullable(itemfrequency).orElse(new Integer(0)) + 1;
        return itemfrequency;
    }
}
