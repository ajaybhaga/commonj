/*
 * Created on Sep 16, 2004
 */
package org.jcommon.lang;

import java.io.*;

/**
 * This class is essentially a wrapper for Double so it can be distinguished that it
 * should be formatted as Currency instead of just a decimal value.
 * 
 * @author Matt Hicks
 */
public class Currency extends Number implements Serializable, Comparable {
    public static final double MAX_VALUE = Double.MAX_VALUE;
    public static final double MIN_VALUE = Double.MIN_VALUE;
    public static final double NaN = Double.NaN;
    public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
    public static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
    public static final double SIZE = Double.SIZE;
    public static final Class TYPE = Double.TYPE;
    
    protected Double value;
    
    public Currency(double value) {
        this.value = new Double(value);
    }
    
    public Currency(Double value) {
        this.value = value;
    }
    
    public Currency(String s) {
        this.value = new Double(s);
    }
    
    public byte byteValue() {
        return value.byteValue();
    }
    
    public int compareTo(Object anotherDouble) {
        return value.compareTo(new Double(((Currency)anotherDouble).doubleValue()));
    }
    
    public double doubleValue() {
        return value.doubleValue();
    }
    
    public boolean equals(Object o) {
        return value.equals(o);
    }
    
    public float floatValue() {
        return value.floatValue();
    }
    
    public int hashCode() {
        return value.hashCode();
    }
    
    public int intValue() {
        return value.intValue();
    }
    
    public boolean isInfinite() {
        return value.isInfinite();
    }
    
    public boolean isNaN() {
        return value.isNaN();
    }
    
    public long longValue() {
        return value.longValue();
    }
    
    public short shortValue() {
        return value.shortValue();
    }
    
    public String toString() {
        return value.toString();
    }
    
    public static int compare(double d1, double d2) {
        return Double.compare(d1, d2);
    }
    
    public static long doubleToLongBits(double value) {
        return Double.doubleToLongBits(value);
    }
    
    public static long doubleToRawLongBits(double value) {
        return Double.doubleToRawLongBits(value);
    }
    
    public static boolean isNaN(double v) {
        return Double.isNaN(v);
    }
    
    public static double longBitsToDouble(long bit) {
        return Double.longBitsToDouble(bit);
    }
    
    public static double parseDouble(String s) {
        return Double.parseDouble(s);
    }
    
    public static String toHexString(double d) {
        return Double.toHexString(d);
    }
    
    public static String toString(double d) {
        return Double.toString(d);
    }
    
    public static Currency valueOf(double d) {
        return new Currency(d);
    }
    
    public static Currency valueOf(String s) {
        return new Currency(s);
    }
}
