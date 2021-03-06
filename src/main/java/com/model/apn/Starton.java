package com.model.apn;

import java.io.IOException;
/**
 * Created by jack on 2017/3/20.
 */
public class Starton {
    /*
    IF [CrossValidation]
        args[0]: -CV
        args[1]: dir_path
        args[2]: file_name
        args[3]: max_fold
        args[4]: MEPA_divide_num
        args[5]: BIO => (-ABC, -ACO, -PSO)
        args[6]: Iteration
        args[7]: Population
        args[8]: ABC parameters "0.1:0.4:0.5"

        args[?]: correlationNet:min_branch:min_correlation

        ex. "java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5"
            "java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5 -ABC 10 10 0.1:0.4:0.5"
            "java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5 -ACO 10 10"
            "java -jar Test.jar -CV C:/Data/test/Wine origin.txt 10 5 -PSO 10 10"
    END IF

    -----------------------------------------------------------------------------------------
    IF [TrainTest]
        args[0]: -TT
        args[1]: dir_path
        args[2]: train_file_name
        args[3]: test_file_name
        args[4]: MEPA_divide_num
        args[5]: BIO => (-ABC, -ACO, -PSO)
        args[6]: Iteration
        args[7]: Population
        args[8]: ABC parameters "0.1:0.4:0.5"

        args[?]: correlationNet:min_branch:min_correlation

        ex. "java -jar Test.jar -TT C:/Data/test/Wine train.txt test.txt 5"
            "java -jar Test.jar -TT C:/Data/test/Wine train.txt test.txt 5 -ABC 10 10 0.1:0.4:0.5"
            "java -jar Test.jar -TT C:/Data/test/Wine train.txt test.txt 5 -ACO 10 10"
            "java -jar Test.jar -TT C:/Data/test/Wine train.txt test.txt 5 -PSO 10 10"
    END IF
     */
    public static void main(String[] args) throws IOException {
//        new Cmd(args);

        testDivide(2);
//        crossValidation_example(1);
    }

    private static String URL = "C:/Data/Biotset/breast";

    private static void testDivide(int num) throws IOException{
        String[] command = ("-CV "+URL+" origin.txt 10 "+num).split("\\s+"); //fake command
        new Cmd(command);
        System.out.println("Original APN");
    }

    private static void crossValidation_example(int div) throws IOException {

        String[] command = ("-CV "+URL+" origin.txt 10 "+div).split("\\s+"); //fake command
//        new Cmd(command);
//        System.out.println("Original APN");

        command = ("-CV "+URL+" origin.txt 10 "+div+" -ABC 10 100 0.1:0.4:0.5").split("\\s+"); //fake command
        new Cmd(command);
        System.out.println("ABC APN");

        command = ("-CV "+URL+" origin.txt 10 "+div+" -ACO 10 100").split("\\s+"); //fake command
        new Cmd(command);
        System.out.println("ACO APN");

        command = ("-CV "+URL+" origin.txt 10 "+div+" -PSO 10 100").split("\\s+"); //fake command
        new Cmd(command);
        System.out.println("PSO APN");
    }

    private static void forTrainTest_example() throws IOException {
        String[] command = "-TT C:/Data/R/Test weka_train.csv weka_test.csv 5".split("\\s+"); //fake command
        new Cmd(command);
    }


}
