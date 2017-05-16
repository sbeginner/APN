package com.model.apn.NetworkStructureTemplate;

/**
 * Created by JACK on 2017/5/16.
 */
public class IrisSampleTempl {
    private int[][] networkStructure;

    public IrisSampleTempl(int[][] networkStructure){
        this.networkStructure = networkStructure;
    }

    public int[][] template1(){

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

        return networkStructure;
    }
}
