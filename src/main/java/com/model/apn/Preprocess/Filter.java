package com.model.apn.Preprocess;

import com.model.apn.DataStructure.Instances;

/**
 * Created by jack on 2017/3/31.
 */
public class Filter{
    public static Instances useFilter(Instances instances, MEPA MEPAFilter){
        //This filter uses MEPA method
        MEPAFilter.setInstances(instances);
        MEPAFilter.useFilter();

        MEPAFilter.MEPAInstancesOutput();

        return MEPAFilter.getInstances();
    }
}
