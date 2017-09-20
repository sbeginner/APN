package com.model.apn.Setup;

/**
 * Created by jack on 2017/3/20.
 */
public class Config extends Setup.Config{

    public static int THRESHOLD_NUM;

    public static int ROOT_PLACE;
    public static int BRANCH_PLACE;
    public static int LEAF_PLACE;

    public static boolean PRINT_TRACETRAVELHISTORY_BTN;
    public static boolean PRINT_DETAIL_BTN;
    public static boolean PROBABILITY_PREDICT_BTN;
    public static boolean KEEP_TOTALRESULT_BY_EACH_FOLD;

    static {
        PROBABILITY_PREDICT_BTN = true;
        PRINT_DETAIL_BTN = false;
        PRINT_TRACETRAVELHISTORY_BTN = true;

        KEEP_TOTALRESULT_BY_EACH_FOLD = true;

        MAX_FOLDNUM = 10;
        RANDOM_SEED = 2;
    }

    //For MEPA and data processing relative config
    static {
        AUTO_MISSINGVALUE_BTN = false;
        INSTANCEORDER_SHUFFLE_BTN = false;
        MEPADATA_OUTPUT_BTN = false;

        FILEPATH = "C:/Data/test/Iris";
        FILENAME = "origin.txt";
        FILETRAINNAME = "train.txt";
        FILETESTNAME = "test.txt";

        DIVIDE_CONSTRAINTNUM = 5;
    }


    //For Init
    static {
        THRESHOLD_NUM = 1000;

        ROOT_PLACE = 0;
        BRANCH_PLACE = 100;
        LEAF_PLACE = 1000;
    }
}
