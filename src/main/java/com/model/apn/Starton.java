package com.model.apn;

import com.model.apn.FileIO.DataInput;

import java.io.IOException;
import java.util.stream.IntStream;

/**
 * Created by jack on 2017/3/20.
 */
public class Starton {

    public static void main(String str[]) throws IOException {
        DataInput dt =new DataInput();

        dt.forKfoldValidationInstance();
        //dt.forTrainTestInstance();
        dt.completeData();
        /*
        dt.forTrainTestInstance();
        dt.forTrainInstance();
        dt.forTestInstance();
        */

        System.out.println();
        System.out.println("Attribute size : "+dt.getInstances().getAttributeMap().size());
        dt.getInstances().getAttributeMap().forEach((k,v)->{
            System.out.println("Attr : " + k + " isString : " +v.getAttributeType()+ " Value : "+v.getAllValue());
        });

        System.out.println();
        IntStream.range(0, dt.getInstances().getAttributeMap().size()).forEach(i->{
            System.out.println("Attr : "+i+" Avg or mode "+dt.getInstances().getAttributeMap().get(i).getMissingValue());
        });

        System.out.println();
        IntStream.range(0, dt.getInstances().getAttributeMap().size()).forEach(i->{
            System.out.println("Attr : "+i+" Avg or mode "+dt.getInstances().getAttributeMap().get(i).getMissingValueTest());
        });

        System.out.println();
        System.out.println(dt.getInstances().getCurrentMode());

        /*
        dt.getInstances().getTestInstanceMap().forEach((k,v)->{
            System.out.println("Ind : " + k+ " value : "+v.getInstanceMap());
        });

        dt.getInstances().getTrainInstanceMap().forEach((k,v)->{
            System.out.println("Ind : " + k + " value : "+v.getInstanceMap());
        });

        dt.getInstances().getInstanceMap().forEach((k,v)->{
            System.out.println("Ind : " + k + " value : "+v.getInstanceMap());
        });

        /*
        dt.getInstances().getTrainInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " +v.getInstanceValue(1));
        });
        dt.getInstances().getTestInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " +v.getInstanceValue(1));
        });
        */

        /*
        System.out.println(dt.getInstances().getTrainInstanceMap().size());
        dt.getInstances().getTrainInstance(0).getInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " + v);
        });
        System.out.println(dt.getInstances().getTestInstanceMap().size());
        dt.getInstances().getTestInstance(6).getInstanceMap().forEach((k,v)->{
            System.out.println("Item : " + k + " Count : " + v);
        });
        */
    }



}
