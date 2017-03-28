package com.model.apn.Math;

import java.math.BigDecimal;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by jack on 2017/3/27.
 */
public class Arithmetic {
    private static final int DEF_DIV_SCALE = 3;

    public static boolean checkCreatable(String value){
        return NumberUtils.isCreatable(value);
    }

    public static double createDouble(String value){
        return round(NumberUtils.createDouble(value));
    }

    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return round(b1.add(b2).doubleValue());
    }

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return round(b1.subtract(b2).doubleValue());
    }

    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return round(b1.multiply(b2).doubleValue());
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("scale must not be negetive");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return round(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("scale must not be negetive");
        }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double round(double v) {
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
