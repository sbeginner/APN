package com.model.apn.NetworkStructure;

import DataStructure.Instances;

import java.util.Arrays;
import java.util.stream.IntStream;

import static Setup.Config.ATTRIBUTE_NUM;
import static Setup.Config.TARGET_ATTRIBUTE;

/**
 * Created by JACK on 2017/5/5.
 */
public class APNNetworkStructure {
    Instances instances;

    public APNNetworkStructure(Instances instances){
        this.instances = instances;
    }

    public void testa(){
        int[][] networkStructure = netStructureExample();
        IntStream.range(0, networkStructure.length).forEach(i -> {
            if(!Arrays.stream(networkStructure[i]).filter(num -> (num > 0)).findAny().isPresent()){
                System.out.println(i);
            }
        });

    }

    private int[][] netStructureExample(){
        int featurePlaceNum = ATTRIBUTE_NUM - 1;
        System.out.println(featurePlaceNum);
        int allPlaceNum = featurePlaceNum + instances.getAttribute(TARGET_ATTRIBUTE).getAllValue().size();
        System.out.println(TARGET_ATTRIBUTE);

        System.out.println(featurePlaceNum+"*"+allPlaceNum);
        int[][] networkStructure = new int[allPlaceNum][featurePlaceNum];
        networkStructure[2][0] = 1;
        networkStructure[2][1] = 1;
        networkStructure[3][0] = 2;
        networkStructure[3][1] = 2;
        networkStructure[4][2] = 3;
        networkStructure[5][2] = 3;
        networkStructure[6][2] = 3;
        networkStructure[4][3] = 4;
        networkStructure[5][3] = 4;
        networkStructure[6][3] = 4;
        for (int i=0;i<networkStructure.length;i++){
            for (int j=0;j<networkStructure[i].length;j++){
                System.out.print(networkStructure[i][j]+" ");
            }
            System.out.println();
        }

        return networkStructure;
    }

}
