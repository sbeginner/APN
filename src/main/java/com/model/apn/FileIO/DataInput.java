package com.model.apn.FileIO;

import com.model.apn.DataStructure.Attribute;
import com.model.apn.DataStructure.Instance;
import com.model.apn.DataStructure.Instances;
import com.model.apn.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.SyncFailedException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by jack on 2017/3/21.
 */
public class DataInput {

    Instances instances;
    Path inputPath = Paths.get(Config.FILEPATH, Config.FILENAME);
    Charset charset = Charset.forName(Config.CHARSETTYPE);

    public DataInput(){
        this.instances = new Instances();
    }

    public void StartTransData(){
        try {
            BufferedReader inputBuffer = Files.newBufferedReader(inputPath, charset);
            setAttributeInfo(inputBuffer);
            setInstanceInfo(inputBuffer);

            IntStream.range(0, Config.ATTRIBUTE_NUM)
                    .forEach(currentAttributeInd -> {
                        instances.getAttributeList().get(currentAttributeInd).transAttrValueSet2AttrValueList();
                    });

            inputBuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(((Attribute)instances.getAttributeList().get(4)).getAllValue());
    }

    private void setAttributeInfo(BufferedReader inputBuffer){

        ArrayList<ArrayList<String>> attrtitleList;
        attrtitleList = inputBuffer.lines()
                .limit(1)
                .map(mapToSplitItem)
                .peek(attrtitleItem -> {
                    IntStream.range(0, attrtitleItem.size())
                            .forEach(attrtitleItemNum -> instances.setAttribute(attrtitleItem.get(attrtitleItemNum)));
                })
                .collect(Collectors.toCollection(ArrayList::new));


        Config.ATTRIBUTE_NUM = attrtitleList.get(0).size();

        //test
        //System.out.println(instances.getAttributeList());
        //IntStream.range(0, instances.getAttributeList().size())
        //        .peek(i -> System.out.println(instances.getAttributeList().get(i).getAttributeName()))
        //        .forEach(System.out::println);
    }

    private void setInstanceInfo(BufferedReader inputBuffer){

        HashSet<String> uniqueWords = new HashSet<String>();

        long InstanceNum = inputBuffer.lines()
                .map(mapToSplitItem)
                .peek(instanceItem -> new Instance(instanceItem, instances))
                .count();

        Config.INSTANCE_NUM = Math.toIntExact(InstanceNum);

        //test
        //System.out.println(instances.getInstanceList());
        //IntStream.range(0, instances.getInstanceList().size())
        //        .peek(i -> System.out.println(instances.getInstanceList().get(i).getInstanceMap()))
        //        .forEach(System.out::println);
    }

    public void setInputPath(String filePath, String fileName){
        this.inputPath = Paths.get(filePath, fileName);
    }

    public void setCharset(String charsetType){
        this.charset = Charset.forName(charsetType);
    }

    private Function<String, ArrayList<String>> mapToSplitItem = (String line) -> {
        //Split item function, for example, [A,B,C] divide by ',' will get A B C ArrayList<String> output
        ArrayList<String> attarList = Pattern.compile("\\s*,\\s*")
                .splitAsStream(line.trim())
                .collect(ArrayList::new,ArrayList::add,ArrayList::addAll);

        return attarList;
    };
}
