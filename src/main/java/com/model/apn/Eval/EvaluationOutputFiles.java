package com.model.apn.Eval;

import DataStructure.Attribute;
import DataStructure.Instances;
import com.model.apn.APNObject.Place;
import com.model.apn.Model.APN;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static Setup.Config.FILEPATH;
import static Setup.Config.TARGET_ATTRIBUTE;

class EvaluationOutputFiles {
    private Instances instances;

    void evaluationOutput(Instances instances, APN APNmodel, int[][] cNetStructure, int[][] cMatrix){
        this.instances = instances;
        output_cNetStructure("output", structureInfo(APNmodel, cNetStructure));
        output_cMatrix("output", confuseMatrixInfo(cMatrix));
    }

    private Path checkOutputDirIsExist(String dirPath, String fileName) {
        File outputRootDir = new File(dirPath);
        if (!outputRootDir.exists()) {
            outputRootDir.mkdir();
        }

        return Paths.get(dirPath, fileName);
    }

    private void output_cNetStructure(String oFilename, String value){
        try {
            BufferedWriter outputBuffer = Files.newBufferedWriter(checkOutputDirIsExist(FILEPATH, oFilename+".cNetStructure"),
                    Charset.forName(Setup.Config.CHARSETTYPE));
            outputBuffer.write(value);
            outputBuffer.flush();
            outputBuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void output_cMatrix(String oFilename, String value){
        try {
            BufferedWriter outputBuffer = Files.newBufferedWriter(checkOutputDirIsExist(FILEPATH, oFilename+".cMatrix"),
                    Charset.forName(Setup.Config.CHARSETTYPE));
            outputBuffer.write(value);
            outputBuffer.flush();
            outputBuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String confuseMatrixInfo(int[][] cMatrix){
        StringBuilder str = new StringBuilder();

        ArrayList<StringBuilder> allValue =
                instances.getAttribute(TARGET_ATTRIBUTE).getAllValue();

        for(StringBuilder v: allValue) {
            str.append(v);
            str.append(",");
        }
        str.replace(str.length() - 1, str.length(), "\n");

        for(int row=0; row < cMatrix.length;row++) {
            for (int col = 0; col < cMatrix[row].length; col++) {
                str.append(cMatrix[row][col]);
                str.append(",");
            }
            str.replace(str.length() - 1, str.length(), "\n");
        }

        return str.toString();
    }

    private String structureInfo(APN APNmodel, int[][] cNetStructure){
        HashMap<Integer, Place> placeMap = APNmodel.getAPNNetStructure().getPlaceMap();
        StringBuilder str = new StringBuilder();

        for(Attribute attr: instances.getAttributeMap().values()){
            str.append(attr.getAttributeName());
            str.append(",");
        }
        str.replace(str.length() - 1, str.length(), "\n");

        for(Place v: placeMap.values()){
            str.append(v.getAttribute().getAttributeName());
            if(v.getAttribute().getIndex() == TARGET_ATTRIBUTE){
                String attrValue = "("+v.getTestAttributeValue()+")";
                str.append(attrValue);
            }
            str.append(",");
        }
        str.replace(str.length() - 1, str.length(), "\n");

        for(int row=0; row < cNetStructure.length;row++) {
            for (int col = 0; col < cNetStructure[row].length; col++) {
                str.append(cNetStructure[row][col]);
                str.append(",");
            }
            str.replace(str.length() - 1, str.length(), "\n");
        }

        return str.toString();
    }
}
