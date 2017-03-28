package com.model.apn.FileIO;

import com.model.apn.Config;
import com.model.apn.DataStructure.Instance;
import com.model.apn.Math.Arithmetic;
import com.sun.deploy.xml.XMLAttributeBuilder;
import org.junit.Test;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.model.apn.Config.ATTRIBUTE_NUM;
import static com.model.apn.Config.AUTO_MISSINGVALUE_BTN;
import static com.model.apn.Math.Arithmetic.createDouble;
import static com.model.apn.Math.Arithmetic.mul;
import static org.junit.Assert.*;

/**
 * Created by jack on 2017/3/28.
 */
public class DataInputTest {

    @Test
    private void setAttributeInfo(BufferedReader inputBuffer){

        //test
        /*
        System.out.println(instances.getAttributeList());
        IntStream.range(0, instances.getAttributeList().size())
               .peek(i -> System.out.println(instances.getAttributeList().get(i).getAttributeName()))
               .forEach(System.out::println);
               */
    }

    private void setTrainInstanceInfo(BufferedReader inputBuffer){

        //test
        //System.out.println(instances.getInstanceList());
        //IntStream.range(0, instances.getInstanceList().size())
        //        .peek(i -> System.out.println(instances.getInstanceList().get(i).getInstanceMap()))
        //        .forEach(System.out::println);
    }

    private void setTestInstanceInfo(BufferedReader inputBuffer){

        //test
        //System.out.println(instances.getInstanceList());
        //IntStream.range(0, instances.getInstanceList().size())
        //        .peek(i -> System.out.println(instances.getInstanceList().get(i).getInstanceMap()))
        //        .forEach(System.out::println);
    }

    private void setInstanceInfo(BufferedReader inputBuffer){
        //test
        //System.out.println(instances.getInstanceList());
        //IntStream.range(0, instances.getInstanceList().size())
        //        .peek(i -> System.out.println(instances.getInstanceList().get(i).getInstanceMap()))
        //        .forEach(System.out::println);
    }

    private void transAttrValueMap(){

        /*
        checkModeAndIsTest(false, false);
        checkModeAndIsTest(false, true);
        checkModeAndIsTest(true, false);
        checkModeAndIsTest(true, true);
        System.out.println(checkCurrentMode()+", "+checkIsTrainTest());
        */
    }

    private void missingValueProcess(boolean checkIsTest){
        //missing value
        //double i = NumberUtils.createDouble(instances.getAttributeMap().get(0).getValue(0).toString());
        //System.out.println(NumberUtils.isCreatable(instances.getAttributeMap().get(0).toString())+" "+instances.getAttributeMap().get(0).getValue(0).toString());
    }

}