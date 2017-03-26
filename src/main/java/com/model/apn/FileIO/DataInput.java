package com.model.apn.FileIO;

import com.model.apn.DataStructure.Attribute;
import com.model.apn.DataStructure.Instance;
import com.model.apn.DataStructure.Instances;
import com.model.apn.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.model.apn.Config.ATTRIBUTE_NUM;
import static com.model.apn.Config.NONVALUE_INTEGER;
import static com.model.apn.Config.UNKNOWNVALUE;
import static com.model.apn.Math.Arithmetic.checkCreatable;
import static com.model.apn.Math.Arithmetic.createDouble;
import static com.model.apn.Math.Arithmetic.mul;

/**
 * Created by jack on 2017/3/21.
 */
public class DataInput {


    private boolean switchSecondTime = false;
    private boolean switchAttributeFound = false;
    private int errorLineCount = -1;
    private Instances instances;
    private Path inputPath = Paths.get(Config.FILEPATH, Config.FILENAME);
    private Path inputTrainPath = Paths.get(Config.FILEPATH, Config.FILETRAINNAME);
    private Path inputTestPath = Paths.get(Config.FILEPATH, Config.FILETESTNAME);
    private Charset charset = Charset.forName(Config.CHARSETTYPE);

    public DataInput(){
        this.instances = new Instances();
    }

    public void forKfoldValidationInstance() throws IOException{
        BufferedReader inputBuffer = Files.newBufferedReader(inputPath, charset);
        setAttributeInfo(inputBuffer);
        switchOnlyFirstTime();
        setInstanceInfo(inputBuffer);

        transAttrValueMap();
        inputBuffer.close();

        //System.out.println(((Attribute)instances.getAttributeList().get(4)).getAllValue());
    }

    public void forKfoldValidationInstance(String filePath) throws IOException{
        this.inputPath = Paths.get(filePath);
        forKfoldValidationInstance();
    }

    public void forTrainInstance() throws IOException{
        BufferedReader inputBuffer = Files.newBufferedReader(inputTrainPath, charset);
        setAttributeInfo(inputBuffer);
        setTrainInstanceInfo(inputBuffer);

        transAttrValueMap();
        inputBuffer.close();
        switchSecondTime();

        //System.out.println(((Attribute)instances.getAttributeMap().get(4)).getAllValue());
    }

    public void forTrainInstance(String filePath) throws IOException{
        this.inputTrainPath = Paths.get(filePath);
        forTrainInstance();
    }

    public void forTestInstance() throws IOException{
        BufferedReader inputBuffer = Files.newBufferedReader(inputTestPath, charset);
        setAttributeInfo(inputBuffer);
        setTestInstanceInfo(inputBuffer);

        transAttrValueMap();
        inputBuffer.close();
        switchSecondTime();

        //System.out.println(((Attribute)instances.getAttributeMap().get(4)).getAllValue());
    }

    public void forTestInstance(String filePath) throws IOException{
        this.inputTestPath = Paths.get(filePath);
        forTestInstance();
    }

    public void forTrainTestInstance(String trainFilePath, String testFilePath) throws IOException{
        forTrainInstance(trainFilePath);
        forTestInstance(testFilePath);
    }

    public void forTrainTestInstance() throws IOException{
        forTrainInstance();
        forTestInstance();
    }

    private void setAttributeInfo(BufferedReader inputBuffer){

        ArrayList<ArrayList<String>> attrtitleList;

        if(checkIsSecondTime()){
            //the implement of train code and test code order,may test code conduct first
            checkAttributeBetweenTrainTest(inputBuffer);
            return;
        }

        attrtitleList = inputBuffer.lines()
                .limit(1)
                .map(mapToSplitItem)
                .peek(attrtitleItem -> {
                    IntStream.range(0, attrtitleItem.size())
                            .forEach(attrtitleItemNum -> instances.setAttribute(attrtitleItem.get(attrtitleItemNum)));
                })
                .collect(Collectors.toCollection(ArrayList::new));

        ATTRIBUTE_NUM = attrtitleList.get(0).size();
        switchAttributeFound();
        //test
        //System.out.println(instances.getAttributeList());
        //IntStream.range(0, instances.getAttributeList().size())
        //        .peek(i -> System.out.println(instances.getAttributeList().get(i).getAttributeName()))
        //        .forEach(System.out::println);
    }

    private void setTrainInstanceInfo(BufferedReader inputBuffer){

        long InstanceNum = inputBuffer.lines()
                .map(mapToSplitItem)
                .peek(instanceItem -> new Instance(instanceItem, instances, true))
                .count();

        Config.INSTANCE_NUM_TRAIN = Math.toIntExact(InstanceNum);

        //test
        //System.out.println(instances.getInstanceList());
        //IntStream.range(0, instances.getInstanceList().size())
        //        .peek(i -> System.out.println(instances.getInstanceList().get(i).getInstanceMap()))
        //        .forEach(System.out::println);
    }

    private void setTestInstanceInfo(BufferedReader inputBuffer){

        long InstanceNum = inputBuffer.lines()
                .map(mapToSplitItem)
                .peek(instanceItem -> new Instance(instanceItem, instances, false))
                .count();

        Config.INSTANCE_NUM_TEST = Math.toIntExact(InstanceNum);

        //test
        //System.out.println(instances.getInstanceList());
        //IntStream.range(0, instances.getInstanceList().size())
        //        .peek(i -> System.out.println(instances.getInstanceList().get(i).getInstanceMap()))
        //        .forEach(System.out::println);
    }

    private void setInstanceInfo(BufferedReader inputBuffer){
        //for k-fold validation

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

    public void setCharset(String charsetType){
        this.charset = Charset.forName(charsetType);
    }

    private void transAttrValueMap(){
        //do something about attribute information processing
        if(!checkIsSecondTime()){
            return;
        }

        IntStream.range(0, ATTRIBUTE_NUM)
                .forEach(currentAttributeInd -> {
                    Attribute curattr = instances.getAttributeMap().get(currentAttributeInd);
                    curattr.transAttrValueSet2AttrValueList();

                    int checkAttrisStr = IntStream.range(0, instances.getAttributeMap().get(currentAttributeInd).getAllValuesize())
                            .filter(currentValuenum -> {
                                //return attribute value when the string type find out
                                String curattrValue = curattr.getValue(currentValuenum).toString();
                                Boolean curValueisStr = !checkCreatable(curattrValue);
                                return curValueisStr;
                            })
                            .findFirst()
                            .orElse(NONVALUE_INTEGER);

                    curattr.setAttributeType(checkAttrisStr >= 0);
                });

        String modeAttrvalue = instances.getAttributeMap()
                .get(4)
                .getAttrValueMap()
                .entrySet()
                .stream()
                .max((currentMax, currentComparenum) -> currentMax.getValue() > currentComparenum.getValue() ? 1 : -1)
                .get()
                .getKey();

        instances.getAttributeMap()
                .get(3)
                .getAttrValueMap()
                .entrySet()
                .stream()
                .peek(item -> {
                    System.out.println(item.getKey()+" "+item.getValue());
                })
                .peek(item ->{
                    System.out.println(mul(createDouble(item.getKey()), item.getValue()));
                })
                .forEach(System.out::println);

        //double i = NumberUtils.createDouble(instances.getAttributeMap().get(0).getValue(0).toString());
        System.out.println(modeAttrvalue);

        //System.out.println(NumberUtils.isCreatable(instances.getAttributeMap().get(0).toString())+" "+instances.getAttributeMap().get(0).getValue(0).toString());
    }

    private Function<String, ArrayList<String>> mapToSplitItem = (String line) -> {
        //Split item function, for example, [A,B,C] divide by ',' will get A B C ArrayList<String> output
        ArrayList<String> attarList = Pattern.compile("\\s*,\\s*")
                .splitAsStream(line.trim())
                .peek(item -> {
                    if(item.isEmpty()){
                        errorAttributeBetweenTrainTest(2);
                    }
                })
                .map(item -> Optional.ofNullable(item).filter(s -> !s.isEmpty()).orElse(UNKNOWNVALUE))
                .collect(ArrayList::new,ArrayList::add,ArrayList::addAll);


        if(checkIsAttributeFound()){
            if(attarList.size() != ATTRIBUTE_NUM){
                errorAttributeBetweenTrainTest(2);
            }
        }
        errorLineCountPlus();

        return attarList;
    };

    private void switchSecondTime(){
        //only for train/test, this function makes sure all the data source(includes train and test data) have been scanned
        // , and then pull out all the attribute value in the second time.
        switchSecondTime = !switchSecondTime;
    }

    private void switchOnlyFirstTime(){
        //only for k-fold validation, the function just scan in one data source at a time
        switchSecondTime = true;
    }

    private void switchAttributeFound(){
        switchAttributeFound = true;
    }

    private boolean checkIsSecondTime(){
        return switchSecondTime;
    }

    private boolean checkIsAttributeFound(){
        return switchAttributeFound;
    }

    private boolean checkAttributeBetweenTrainTest(BufferedReader inputBuffer){
        boolean isTrainTestSameAttribute;
        ArrayList<ArrayList<String>> attrtitleList;
        ArrayList<String> existAttributeList;

        //check for attribute number between training data and testing data, same as 'true', different as 'false'
        attrtitleList = inputBuffer.lines()
                .limit(1)
                .map(mapToSplitItem)
                .collect(Collectors.toCollection(ArrayList::new));

        isTrainTestSameAttribute = ATTRIBUTE_NUM == attrtitleList.get(0).size();
        if(!isTrainTestSameAttribute){
            return errorAttributeBetweenTrainTest(0);
        }

        //check for attribute value between training data and testing data under the same attribute number, same as 'true', different as 'false'
        //Map convert to list
        existAttributeList = (instances.getAttributeMap()).entrySet().stream()
                .map(x -> x.getValue().getAttributeName().toString())
                .collect(Collectors.toCollection(ArrayList::new));

        isTrainTestSameAttribute = attrtitleList.get(0).containsAll(existAttributeList);
        if(!isTrainTestSameAttribute){
            return errorAttributeBetweenTrainTest(1);
        }

        return true;
    }

    private void errorLineCountPlus(){
        errorLineCount+=1;
    }

    private int geterrorLine(){
        return errorLineCount;
    }

    private boolean errorAttributeBetweenTrainTest(int errorcode){
        try {
            errorAttributeBetweenTrainTestException(errorcode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void errorAttributeBetweenTrainTestException(int errorcode) throws Exception{
        switch (errorcode){
            case 0:
                throw new Exception("Train and Test Attribute no match! check by length");
            case 1:
                throw new Exception("Train and Test Attribute no match! check by attribute value");
            case 2:
                throw new Exception("Instance item has some problem, may lose some item, line : "+geterrorLine());
        }
    }

    public Instances getInstances(){
        return instances;
    }

}
