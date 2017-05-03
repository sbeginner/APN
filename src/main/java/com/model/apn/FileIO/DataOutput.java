package com.model.apn.FileIO;

import com.model.apn.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by JACK on 2017/5/4.
 */
public class DataOutput{

    private boolean currentMode = false;
    private int currentFoldValid = 0;
    private Path outputPath;
    private String outputRootPath = Config.FILEPATH+"/output/";
    private String outputTrainPath = outputRootPath+"/train/";
    private String outputTestPath = outputRootPath+"/test/";
    private Charset charset = Charset.forName(Config.CHARSETTYPE);

    public DataOutput(boolean currentMode, int currentFoldValid){
        this.currentMode = currentMode;
        this.currentFoldValid = currentFoldValid;

        checkOutputDirIsExist(outputRootPath);
        checkOutputDirIsExist(outputTrainPath);
        checkOutputDirIsExist(outputTestPath);
    }

    private void checkOutputDirIsExist(String dirPath){
        File outputRootDir = new File(dirPath);
        if(!outputRootDir.exists()){
            outputRootDir.mkdir();
        }
    }

    private void outputData(StringBuilder MembershipNameStr, Path outputPath){
        BufferedWriter outputBuffer;

        try {
            outputBuffer = Files.newBufferedWriter(outputPath, charset);
            outputBuffer.write(MembershipNameStr.toString());
            outputBuffer.flush();
            outputBuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputTrainMembership(StringBuilder MembershipNameStr, String fileName){
        outputPath = Paths.get(outputTrainPath, "Fold"+currentFoldValid+fileName+".txt");
        outputData(MembershipNameStr, outputPath);
    }

    public void outputTestMembership(StringBuilder MembershipNameStr, String fileName){
        outputPath = Paths.get(outputTestPath, "Fold"+currentFoldValid+fileName+".txt");
        outputData(MembershipNameStr, outputPath);
    }


}
