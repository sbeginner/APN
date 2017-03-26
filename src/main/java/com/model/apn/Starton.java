package com.model.apn;

import com.model.apn.FileIO.DataInput;

import java.io.IOException;

/**
 * Created by jack on 2017/3/20.
 */
public class Starton {

    public static void main(String str[]) throws IOException {
        DataInput dt =new DataInput();


        dt.forTrainTestInstance();

        System.out.println(dt.getInstances().getAttributeMap().size());
        dt.getInstances().getAttributeMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " +v.getAttributeType()+ ", "+v.getAllValue());
        });
        System.out.println(dt.getInstances().getTrainInstanceMap().size());
        dt.getInstances().getTrainInstance(0).getInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " + v);
        });
        System.out.println(dt.getInstances().getTestInstanceMap().size());
        dt.getInstances().getTestInstance(6).getInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " + v);
        });

        /*
        dt.forTrainInstance();
        dt.forTestInstance();

        System.out.println(dt.getInstances().getAttributeMap()+""+dt.getInstances().getAttribute(4).getAllValue());
        System.out.println(dt.getInstances().getTrainInstanceMap());
        System.out.println(dt.getInstances().getTestInstanceMap());
        */


        //dt.forKfoldValidationInstance();
        /*
        System.out.println(dt.getInstances().getAttributeMap()+""+dt.getInstances().getAttribute(4).getAllValue());
        dt.getInstances().getAttributeMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " + v.getAllValue());
        });
        System.out.println(dt.getInstances().getInstanceMap());
        */
    }



}
