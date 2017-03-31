package com.model.apn.Preprocess;

import com.model.apn.DataStructure.Instances;

/**
 * Created by jack on 2017/3/31.
 */
public class Filter{
    public static Instances useFilter(Instances instances, MEPA mepaFilter){
        mepaFilter.setInstances(instances);
        mepaFilter.useFilter();
        return mepaFilter.getInstances();
    }
}
