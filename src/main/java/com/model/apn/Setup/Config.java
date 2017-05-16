package com.model.apn.Setup;

/**
 * Created by jack on 2017/3/20.
 */
public class Config extends Setup.Config{

    public static int THRESHOLD_NUM = 1000;

    public static int ROOT_PLACE = 0;
    public static int BRANCH_PLACE = 100;
    public static int LEAF_PLACE = 1000;


    public static boolean PRINT_TRACETRAVELHISTORY_BTN = true;
    public static boolean PRINT_DETAIL_BTN = false;
    public static boolean PROBABILITY_PREDICT_BTN = true;

    public Config(){
        AUTO_MISSINGVALUE_BTN = false;
        INSTANCEORDER_SHUFFLE_BTN = true;
        MEPADATA_OUTPUT_BTN = false;

        FILEPATH = "C:/Data/test";
        FILENAME = "origin.txt";
        FILETRAINNAME = "train.txt";
        FILETESTNAME = "test.txt";

        DIVIDE_CONSTRAINTNUM = 6;
        MAX_FOLDNUM = 10;
        RANDOM_SEED = 1;
    }
}
