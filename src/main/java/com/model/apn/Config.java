package com.model.apn;

import com.model.apn.Math.Arithmetic;

/**
 * Created by jack on 2017/3/20.
 */
public class Config {
     public static int ATTRIBUTE_NUM = 10;
     public static int HALF_ATTRIBUTE_NUM = (int)Arithmetic.div(ATTRIBUTE_NUM, 2);
     public static int ATTRIBUTEVALUE_NUM = 50;
     public static int TARGET_ATTRIBUTE = 10;    //The target attribute index
     public static int INSTANCE_NUM = 1000;
     public static int INSTANCE_NUM_TRAIN = 1000;
     public static int INSTANCE_NUM_TEST = 1000;
     public static int NONVALUE_INTEGER = -1;
     public static int MAX_FOLDNUM = 10;
     public static int RANDOM_SEED = 1994;


     public static String FILEPATH = "C:/Data/test4";
     public static String FILENAME = "origin.txt";
     public static String FILETRAINNAME = "train.txt";
     public static String FILETESTNAME = "test.txt";
     public static String CHARSETTYPE = "UTF-8";
     public static String UNKNOWNVALUE = "NA";

     public static boolean AUTO_MISSINGVALUE_BTN = true;
     public static boolean INSTANCEORDER_SHUFFLE_BTN = true;
}
