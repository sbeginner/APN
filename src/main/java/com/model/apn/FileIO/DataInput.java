package com.model.apn.FileIO;

import com.model.apn.DataStructure.Attribute;
import com.model.apn.DataStructure.Instance;
import com.model.apn.DataStructure.Instances;
import com.model.apn.Config;
import com.model.apn.Math.Arithmetic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.model.apn.Config.*;
import static com.model.apn.Math.Arithmetic.checkCreatable;
import static com.model.apn.Math.Arithmetic.createDouble;
import static com.model.apn.Math.Arithmetic.mul;

/**
 * Created by jack on 2017/3/21.
 */
public class DataInput extends DataInputException {

    private Instances instances;
    private Path inputPath = Paths.get(Config.FILEPATH, Config.FILENAME);
    private Path inputTrainPath = Paths.get(Config.FILEPATH, Config.FILETRAINNAME);
    private Path inputTestPath = Paths.get(Config.FILEPATH, Config.FILETESTNAME);
    private Charset charset = Charset.forName(Config.CHARSETTYPE);

    public DataInput(){
        this.instances = new Instances();
    }


    public void forKfoldValidationInstance() throws IOException{
        switchForKFoldvalidation(true);
        errorLineCountInit();

        BufferedReader inputBuffer = Files.newBufferedReader(inputPath, charset);
        setAttributeInfo(inputBuffer);
        setInstanceInfo(inputBuffer);

        switchOnlyFirstTime();    //Always becomes true

        transAttrValueMap();

        inputBuffer.close();
    }

    public void forKfoldValidationInstance(String filePath) throws IOException{
        this.inputPath = Paths.get(filePath);
        forKfoldValidationInstance();
    }

    public void forTrainInstance() throws IOException{
        switchForTrainTest(true);
        errorLineCountInit();

        BufferedReader inputBuffer = Files.newBufferedReader(inputTrainPath, charset);
        setAttributeInfo(inputBuffer);
        setTrainInstanceInfo(inputBuffer);

        transAttrValueMap();

        switchSecondTime();    //The next becomes the opposite of current state, for checking something like attribute member are matched or other else...

        inputBuffer.close();
    }

    public void forTrainInstance(String filePath) throws IOException{
        this.inputTrainPath = Paths.get(filePath);
        forTrainInstance();
    }

    public void forTestInstance() throws IOException{
        switchForTrainTest(false);
        errorLineCountInit();

        BufferedReader inputBuffer = Files.newBufferedReader(inputTestPath, charset);
        setAttributeInfo(inputBuffer);
        setTestInstanceInfo(inputBuffer);

        transAttrValueMap();

        switchSecondTime();    //The next becomes the opposite of current state, for checking something like attribute member are matched or other else...

        inputBuffer.close();
    }

    public void forTestInstance(String filePath) throws IOException{
        this.inputTestPath = Paths.get(filePath);
        forTestInstance();
    }

    public void forTrainTestInstance() throws IOException{
        forTrainInstance();
        forTestInstance();
    }

    public void forTrainTestInstance(String trainFilePath, String testFilePath) throws IOException{
        forTrainInstance(trainFilePath);
        forTestInstance(testFilePath);
    }


    public void setCharset(String charsetType){
        this.charset = Charset.forName(charsetType);
    }

    private void setAttributeInfo(BufferedReader inputBuffer){
        //The first line is title for attribute name
        //This function builds attributes.
        ArrayList<String> attributeList;
        attributeList = inputBuffer.lines()
                .limit(1)
                .map(mapToSplitItem)
                .flatMap(attributeNameList -> attributeNameList.stream())
                .collect(Collectors.toCollection(ArrayList::new));

        if(checkIsSecondTime()){
            //To avoid some problem about the order whether train command or test command first.
            //check the attribute both test and train are matched
            isBothAttributeMatch(attributeList);
            return;
        }

        attributeList.forEach(unitItem -> instances.setAttribute(unitItem));    //Set attribute only once
        ATTRIBUTE_NUM = Math.toIntExact(attributeList.size());    //Reset attribute number
        TARGET_ATTRIBUTE = ATTRIBUTE_NUM - 1;    //For default, the last one is the target attribute
        switchAttributeFound();    //Switch on, cause the attribute is found
    }

    private void setTrainInstanceInfo(BufferedReader inputBuffer){
        //for train-test validation
        //Same inputBuffer as setAttributeInfo, therefore, it's already skip the first line.
        //This function builds train instances.
        long InstanceNum = inputBuffer.lines()
                .map(mapToSplitItem)
                .peek(instanceItem -> new Instance(instanceItem, instances, true))
                .count();

        Config.INSTANCE_NUM_TRAIN = Math.toIntExact(InstanceNum);    //Reset train instance number
    }

    private void setTestInstanceInfo(BufferedReader inputBuffer){
        //for train-test validation
        //Same inputBuffer as setAttributeInfo, therefore, it's already skip the first line.
        //This function builds test instances.
        long InstanceNum = inputBuffer.lines()
                .map(mapToSplitItem)
                .peek(instanceItem -> new Instance(instanceItem, instances, false))
                .count();

        Config.INSTANCE_NUM_TEST = Math.toIntExact(InstanceNum);
    }

    private void setInstanceInfo(BufferedReader inputBuffer){
        //for k-fold validation
        //Same inputBuffer as setAttributeInfo, therefore, it's already skip the first line.
        //This function builds test instances.
        long InstanceNum = inputBuffer.lines()
                .map(mapToSplitItem)
                .peek(instanceItem -> new Instance(instanceItem, instances))
                .count();

        Config.INSTANCE_NUM = Math.toIntExact(InstanceNum);
    }

    private boolean isBothAttributeMatch(ArrayList<String> currentAttributeList){
        //check train-test attribute is match or not
        ArrayList<String> existAttributeList;
        boolean isTrainTestSameAttribute;
        int attributeNum = currentAttributeList.size();

        //problem 1
        //check for attribute number between training data and testing data, same as 'true', different as 'false'
        isTrainTestSameAttribute = (ATTRIBUTE_NUM == attributeNum);
        if(!isTrainTestSameAttribute){
            return errorAttributeBetweenTrainTest(0);
        }

        //problem 2
        //check for attribute value between training data and testing data under the same attribute number, same as 'true', different as 'false'
        //Map convert to list
        existAttributeList = instances.getAttributeMap()
                .entrySet()
                .stream()
                .map(x -> x.getValue().getAttributeName().toString())
                .collect(Collectors.toCollection(ArrayList::new));

        isTrainTestSameAttribute = existAttributeList.containsAll(currentAttributeList);
        if(!isTrainTestSameAttribute){
            return errorAttributeBetweenTrainTest(1);
        }

        return true;
    }

    private Function<String, ArrayList<String>> mapToSplitItem = (String line) -> {
        //Split item function, for example, [A,B,C] divide by ',' will get A B C ArrayList<String> output
        HashSet<String> emptyItemSet= new HashSet(HALF_ATTRIBUTE_NUM);
        HashSet<String> emptyItemSetTest= new HashSet(HALF_ATTRIBUTE_NUM);

        ArrayList<String> attarList = Pattern.compile("\\s*,\\s*")
                .splitAsStream(line.trim())
                .peek(item -> {
                    if(item.isEmpty()){
                        if((checkIsTrainTest() || checkIsKFoldvalidation())){
                            //for train-test mode: train, or for k-fold validation mode
                            emptyItemSet.add(item);
                        }
                        if(!checkIsTrainTest() && !checkIsKFoldvalidation()){
                            //for train-test mode: test
                            emptyItemSetTest.add(item);
                        }
                    }
                })
                .map(item -> Optional.ofNullable(item).filter(s -> !s.isEmpty()).orElse(UNKNOWNVALUE))
                .collect(ArrayList::new,ArrayList::add,ArrayList::addAll);

        if(checkIsAttributeFound()) {
            //Only for instance processing work
            if (attarList.size() != ATTRIBUTE_NUM) {
                //Instance loss item, detect by comma loss
                errorAttributeBetweenTrainTest(2);
            }

            if (emptyItemSet.size() > 0 || emptyItemSetTest.size() > 0) {
                //Store missing value position, line and attribute

                ArrayList<Integer> missingValueAttrInd = IntStream.range(0, ATTRIBUTE_NUM)
                        .filter(currentAttributeInd -> UNKNOWNVALUE.equals(attarList.get(currentAttributeInd)))
                        .boxed()
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                instances.setMissingValueMap(geterrorLine(), missingValueAttrInd, checkModeAndIsTest(checkCurrentMode(), checkIsTrainTest()));

                if(!AUTO_MISSINGVALUE_BTN){
                    errorAttributeBetweenTrainTest(3);
                }
            }
        }

        errorLineCountPlus();

        return attarList;
    };


    private void transAttrValueMap(){
        //Do something about attribute information processing and other works.
        if(!checkIsSecondTime()){
            return;
        }

        //Set current mode, train-test as true, k fold validation as false.
        instances.setCurrentMode(checkCurrentMode());

        //Transfer attribute value set type to arraylist type ,and set attribute type either digital or string.
        detectAttributeType();

        if(!AUTO_MISSINGVALUE_BTN){
            return;
        }

        //Auto complete missing value
        completeData();
    }

    private void detectAttributeType(){
        //Detect and set attribute type
        IntStream.range(0, ATTRIBUTE_NUM)
                .forEach(currentAttributeInd -> {
                    Attribute curattr = instances.getAttributeMap().get(currentAttributeInd);
                    //Transfer attribute value set type to arraylist type.
                    curattr.transAttrValueSet2AttrValueList();

                    //Find out all the value under each attribute is digital or string.
                    int checkAttrisStr = IntStream.range(0, instances.getAttributeMap().get(currentAttributeInd).getAllValueSize())
                            .filter(currentValuenum -> {
                                //return attribute value when the string type find out, when the string is detected then break the loop
                                String curattrValue = curattr.getValue(currentValuenum).toString();
                                Boolean curValueisStr = !checkCreatable(curattrValue);
                                return curValueisStr;
                            })
                            .findAny()
                            .orElse(NONVALUE_INTEGER);

                    //Variable: checkAttrisStr, -1 as Digital, >= 0 as String.
                    curattr.setAttributeType(checkAttrisStr >= 0);

                    //Create digital list if the type is digital
                    curattr.createDoubleList(!curattr.getAttributeType());
                });
    }

    private void missingValueProcess(){

        missingValueProcess(checkModeAndIsTest(checkCurrentMode(), checkIsTrainTest()));
        if(checkCurrentMode()){
            //only for train-test: test
            missingValueProcess(!checkModeAndIsTest(checkCurrentMode(), checkIsTrainTest()));
        }
    }

    private void missingValueProcess(boolean checkIsTest){
        //Missing value

        //For String type, choose the mode one
        IntStream.range(0, ATTRIBUTE_NUM)
                .filter(currentAttributeInd -> instances.getAttributeMap().get(currentAttributeInd).getAttributeType())
                .forEach(currentAttributeInd -> {
                    String modeAttrvalue = instances.getAttributeMap()
                            .get(currentAttributeInd)
                            .getAttrValueMap(checkIsTest)
                            .entrySet()
                            .stream()
                            .max((currentMax, currentComparenum) -> currentMax.getValue() > currentComparenum.getValue() ? 1 : -1)
                            .get()
                            .getKey();

                    instances.getAttributeMap()
                            .get(currentAttributeInd)
                            .setformissingValue(modeAttrvalue, checkIsTest);
                });

        //For Digital type, choose the average
        Map<Integer, Long> attributeMissingFrequency = instances.getmissingValueMap(checkIsTest)
                .entrySet()
                .stream()
                .map(item -> item.getValue())
                .flatMap(item -> item.stream())
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));

        IntStream.range(0, ATTRIBUTE_NUM)
                .filter(currentAttributeInd -> !instances.getAttributeMap().get(currentAttributeInd).getAttributeType())
                .forEach(currentAttributeInd -> {

                    double countValueFrequency = instances.getAttributeMap()
                            .get(currentAttributeInd)
                            .getAttrValueMap(checkIsTest)
                            .entrySet()
                            .stream()
                            .mapToDouble(item -> mul(createDouble(item.getKey()), item.getValue())).sum();

                    double totalnum = setTotalnum(checkIsTest, attributeMissingFrequency, currentAttributeInd);

                    double average = Arithmetic.div(countValueFrequency, totalnum);

                    instances.getAttributeMap().get(currentAttributeInd).setformissingValue(average, checkIsTest);
                });
    }

    private double setTotalnumSelectInMode(double instancenum, Map<Integer, Long> attributeMissingFrequency, int currentAttributeInd){
        if(attributeMissingFrequency.containsKey(currentAttributeInd)){
            return Arithmetic.sub(instancenum, attributeMissingFrequency.get(currentAttributeInd));
        }else{
            return instancenum;
        }
    }

    private double setTotalnum(boolean checkIsTest,  Map<Integer, Long> attributeMissingFrequency, int currentAttributeInd){
        if(checkCurrentMode()){
            if(checkIsTest){
                return setTotalnumSelectInMode(INSTANCE_NUM_TEST, attributeMissingFrequency, currentAttributeInd);
            }else{
                return setTotalnumSelectInMode(INSTANCE_NUM_TRAIN, attributeMissingFrequency, currentAttributeInd);
            }
        }else {
            return setTotalnumSelectInMode(INSTANCE_NUM, attributeMissingFrequency, currentAttributeInd);
        }
    }


    public void completeData(){
        //Missing value process
        missingValueProcess();
        //Missing value process
        completeDataMethod_Avg();
    }

    private void completeDataMethod_Avg(){
        //Average method for digital, mode method for string
        if(checkCurrentMode()){
            //train-test mode
            //train-test: train
            instances.getmissingValueMap(false)
                    .entrySet()
                    .stream()
                    .forEach(i -> {
                        instances.getTrainInstance(i.getKey()).changeItemValue(i.getValue(),false);
                    });

            //train-test: test
            instances.getmissingValueMap(true)
                    .entrySet()
                    .stream()
                    .forEach(i -> {
                        instances.getTestInstance(i.getKey()).changeItemValue(i.getValue(),true);
                    });
        }else{
            //k-fold validation mode
            instances.getmissingValueMap(false)
                    .entrySet()
                    .stream()
                    .forEach(i -> {
                        instances.getInstance(i.getKey()).changeItemValue(i.getValue(),false);
                    });
        }
    }

    public Instances getInstances(){
        return instances;
    }

}
