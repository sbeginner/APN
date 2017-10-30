package com.model.apn;

import DataStructure.Instances;
import FileIO.DataInput;
import com.model.apn.BionicsMethod.ABC;
import com.model.apn.BionicsMethod.ACO;
import com.model.apn.BionicsMethod.PSO;
import com.model.apn.Eval.Evaluation;
import com.model.apn.Model.APN;
import com.model.apn.Setup.Config;

import java.io.IOException;
import java.util.Objects;

import static Setup.Config.MAX_FOLDNUM;

/**
 * Created by jack on 2017/3/20.
 */
public class Starton {
    /*
    IF [CV]
        args[0]: is CV
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
    IF [CV]
        args[0]: is CV
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
     */
    public static void main(String[] args) throws IOException {
//        new Cmd(args);

//        crossValidation();
        TT();
    }

    private static void crossValidation() throws IOException {
        String[] command = "-CV C:/Data/Biotset/Wine origin.txt 10 5".split("\\s+");
        new Cmd(command);
    }

    private static void TT() throws IOException {
        String[] command = "-TT C:/Data/test/Wine train.txt test.txt 5".split("\\s+");
        command = "-TT C:/Data/test/Wine train.txt test.txt 5 -ABC 10 10 0.1:0.4:0.5".split("\\s+");
        new Cmd(command);
    }


}
