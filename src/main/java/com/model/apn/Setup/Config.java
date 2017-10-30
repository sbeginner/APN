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

    public Config(String[] args, boolean isCV){
        FILEPATH = args[1];
        if(isCV){
            FILENAME = args[2];
            MAX_FOLDNUM = Integer.valueOf(args[3]);
        }else {
            FILETRAINNAME = args[2];
            FILETESTNAME = args[3];
        }
        DIVIDE_CONSTRAINTNUM = Integer.valueOf(args[4]);
    }


    static {
        PROBABILITY_PREDICT_BTN = true;
        PRINT_DETAIL_BTN = false;
        PRINT_TRACETRAVELHISTORY_BTN = false;
        KEEP_TOTALRESULT_BY_EACH_FOLD = true;

        //For MEPA and data processing relative config
        INSTANCEORDER_SHUFFLE_BTN = false;
        MEPADATA_OUTPUT_BTN = false;
    }

    //For Init
    static {
        THRESHOLD_NUM = 1000;
        ROOT_PLACE = 0;
        BRANCH_PLACE = 100;
        LEAF_PLACE = 1000;
    }
}
