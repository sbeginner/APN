package com.model.apn.NetworkStructureTemplate;

import DataStructure.Instance;
import DataStructure.Instances;
import com.model.apn.Setup.Config;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import static com.model.apn.Setup.Config.ATTRIBUTE_NUM;
import static com.model.apn.Setup.Config.TARGET_ATTRIBUTE;
import static com.model.apn.Setup.Config.THRESHOLD_NUM;

import java.util.*;

/**
 * Created by JACK on 2017/5/16.
 */

class Attribute{
    private int id;
    private int father_id;
    private List<Double> data;
    private double correlation;

    Attribute(int id,  List<Double> data){
        this.id = id;
        this.data = data;
    }

    public void setCorrelation(double correlation){
        this.correlation = correlation;
    }

    public void setFatherId(int father_id){
        this.father_id = father_id;
    }

    public int getId(){
        return this.id;
    }

    public int getFatherId(){
        return this.father_id;
    }

    public List<Double> getData(){
        return this.data;
    }

    public double getCorrelation(){
        return this.correlation;
    }
}

public class IrisSampleTempl {
    private int FEATUREPLACENUM, ALLPLACENUM;
    private int[][] networkStructure;
    private Instances instances;

    public IrisSampleTempl(int[][] networkStructure, Instances instances){
        this.networkStructure = networkStructure;
        this.instances = instances;
        init();
    }

    public int[][] template1(){
        /*
        networkStructure[2][0] = 0;
        networkStructure[2][1] = 100;

        networkStructure[3][0] = 10;
        networkStructure[3][1] = 1;

        networkStructure[4][2] = 2;
        networkStructure[4][3] = 3;

        networkStructure[5][2] = 4;
        networkStructure[5][2] = 5;

        networkStructure[6][2] = 6;
        networkStructure[6][2] = 7;
        */

        return main();
    }


    private int[][] main(){

        List<Attribute> all_dat = data();
        Attribute target = all_dat.get(all_dat.size()-1);

        Queue<Attribute> head_member = new LinkedList<>();
        Queue<Attribute> history_member = new LinkedList<>();
        List<Attribute> temp_member = new ArrayList<>(all_dat);

        head_member.offer(target);
        temp_member.remove(target);

        while (!head_member.isEmpty() && !temp_member.isEmpty()){

            for(Attribute item : temp_member){
                item.setCorrelation(Math.abs(getPearson(item.getData(), head_member.peek().getData())));
                item.setFatherId(head_member.peek().getId());
            }

            head_member.poll();
            temp_member.sort(Comparator.comparing(Attribute::getCorrelation).reversed());

            int Max = 2;
            List<Attribute> temp_member_t = new ArrayList<>(temp_member);
            if(temp_member.size() >= Max){

                int initMember = head_member.size();

                for(Attribute item : temp_member){
                    if(item.getCorrelation()>0.9){
                        temp_member_t.remove(item);
                        head_member.offer(item);
                        history_member.offer(item);
                    }else {
                        break;
                    }
                }

                if(head_member.size() == initMember){
                    int cnt = 0;
                    for(Attribute item : temp_member){
                        if(cnt >= Max){
                            break;
                        }

                        temp_member_t.remove(item);
                        head_member.offer(item);
                        history_member.offer(item);
                        cnt++;
                    }
                }

            }else {
                for(Attribute item : temp_member){
                    temp_member_t.remove(item);
                    head_member.offer(item);
                    history_member.offer(item);
                }
            }
            temp_member = temp_member_t;
        }


        int[][] arr = networkStructure;
        int cnt = 0;
        for(Attribute item: history_member){
            System.out.println(item.getFatherId()+" "+item.getId());
            arr[item.getFatherId()][item.getId()] = cnt++;
        }

        for(int i = FEATUREPLACENUM + 1; i < ALLPLACENUM; i++){
            /*
            for(int j=0; j < arr[i].length; j++){
                if(arr[history_member.size()][j] != -1){
                    arr[i][j] = cnt++;
                }
            }*/
            arr[i] = arr[history_member.size()];
        }

        return arr;
    }

    private void init(){
        FEATUREPLACENUM = ATTRIBUTE_NUM - 1;
        ALLPLACENUM = FEATUREPLACENUM + instances.getAttribute(TARGET_ATTRIBUTE).getAllValue().size();
    }

    private List<Attribute> data(){

        HashMap<Integer, List<Double>> dataset = new HashMap<>();
        for(int attributeId = 0; attributeId < ATTRIBUTE_NUM; attributeId++){
            dataset.put(attributeId, new ArrayList<>());
        }

        for(Instance item: instances.getTrainInstanceMap().values()){
            for(int attributeId = 0; attributeId < ATTRIBUTE_NUM; attributeId++){

                if(instances.getAttribute(attributeId).getAttributeType()){
                    //string type
                    String str = item.getInstanceValue(attributeId).toString();
                    double valueId = (double) instances.getAttribute(attributeId).getAttrValueIndByString(str);
                    dataset.get(attributeId).add(valueId);

                }else {
                    //double type
                    dataset.get(attributeId).add(item.getInstanceDigitalValue(attributeId));
                }
            }
        }

        List<Attribute> all_dat = new ArrayList<>();
        dataset.forEach((key, value) -> all_dat.add(new Attribute(key, value)));

        return all_dat;
    }

    private void printStructureValue(int[][] networkStructure){

        System.out.format("%6s", " ");
        for (int i=0;i<networkStructure[0].length;i++)
            System.out.format("%6s","P."+i);
        System.out.println();

        for (int i=0;i<networkStructure.length;i++){
            System.out.format("%6s","P."+i);
            for (int j=0;j<networkStructure[i].length;j++){
                System.out.format("%6d", networkStructure[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private double getPearson(List<Double> list1, List<Double> list2){
        PearsonsCorrelation correlation = new PearsonsCorrelation();
        double c = correlation.correlation(getArray(list1),getArray(list2));

        return c;
    }

    private double[] getArray(List<Double> list){
        double[] array = new double[list.size()];

        int index = 0;
        for(double d : list)
            array[index++] = d;

        return array;
    }
}
