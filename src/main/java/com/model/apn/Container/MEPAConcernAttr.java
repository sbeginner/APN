package com.model.apn.Container;

/**
 * Created by jack on 2017/3/31.
 */
public class MEPAConcernAttr {
    double concernAttribute;
    StringBuilder targetAttribute;

    MEPAConcernAttr(double concernAttribute, StringBuilder targetAttribute){
        this.concernAttribute = concernAttribute;
        this.targetAttribute = targetAttribute;
    }

    public double getConcernAttribute(){
        return concernAttribute;
    }

    public String getTargetAttributeString(){
        return targetAttribute.toString();
    }
}
