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
        crossValidation_example();
    }

    private static void crossValidation_example() throws IOException {
        String[] command = "-CV C:/Data/Biotset/Iris origin.txt 10 5".split("\\s+"); //fake command
        new Cmd(command);
    }

    private static void forTrainTest_example() throws IOException {
        String[] command = "-TT C:/Data/test/Wine train.txt test.txt 5".split("\\s+"); //fake command
        new Cmd(command);
    }


}
