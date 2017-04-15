package com.model.apn.FileIO;

import com.model.apn.DataStructure.Instances;
import org.junit.Test;

import java.io.IOException;

import static com.model.apn.Config.ATTRIBUTE_NUM;
import static com.model.apn.Config.INSTANCE_NUM;
import static org.junit.Assert.*;

/**
 * Created by jack on 2017/3/28.
 */
public class DataInputTest {

    @Test
    public void TestTTmode() throws IOException {

        String str1 = this.getClass().getResource("/TTmodeData/Testdata1").getPath().replaceFirst("/", "");
        String str2 = this.getClass().getResource("/TTmodeData/Traindata1").getPath().replaceFirst("/", "");
        //System.out.println(str1);
        //System.out.println(str2);

        DataInput dt = new DataInput();
        dt.forTrainTestInstance(str1, str2);
        dt.getInstances();
        Instances instances = dt.getInstances();

        //System.out.println(instances.getAttributeMap().size() + ", " + ATTRIBUTE_NUM);
        assertEquals(instances.getAttributeMap().size(), ATTRIBUTE_NUM);
        assertEquals(instances.getTestInstanceMap().size() + instances.getTrainInstanceMap().size(), INSTANCE_NUM);



        dt.completeData();
    }

    @Test
    public void Testkfoldmode() throws IOException {

        String str1 = this.getClass().getResource("/kFoldmodeData/kFolddata1").getPath().replaceFirst("/", "");
        //System.out.println(str1);

        DataInput dt =new DataInput();
        dt.forKfoldValidationInstance(str1);
        dt.completeData();
        Instances instances = dt.getInstances();

        assertEquals(instances.getAttributeMap().size(), ATTRIBUTE_NUM);
        assertEquals(instances.getInstanceMap().size(), INSTANCE_NUM);
    }

}