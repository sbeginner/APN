package com.model.apn.NetworkStructureTemplate;

/**
 * Created by JACK on 2017/5/16.
 */
public class brainwekaRSSampleTempl {
    private int[][] networkStructure;

    public brainwekaRSSampleTempl(int[][] networkStructure){
        this.networkStructure = networkStructure;
    }

    public int[][] template1(){

        networkStructure[5][0] = 0;
        networkStructure[5][1] = 0;
        networkStructure[5][2] = 0;
        networkStructure[5][7] = 0;
        networkStructure[5][8] = 0;

        networkStructure[4][0] = 1;
        networkStructure[4][1] = 1;
        networkStructure[4][2] = 1;
        networkStructure[4][7] = 1;
        networkStructure[4][8] = 1;

        networkStructure[4][5] = 2;
        networkStructure[4][6] = 2;

        networkStructure[9][3] = 4;
        networkStructure[9][4] = 4;

        networkStructure[10][4] = 3;
        networkStructure[10][5] = 3;
        networkStructure[10][6] = 3;


        return networkStructure;
    }
}
