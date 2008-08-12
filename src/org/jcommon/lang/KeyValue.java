/*
 * Created on May 24, 2005
 */
package org.jcommon.lang;

/**
 * @author Matt Hicks
 */
public class KeyValue {
    private String key;
    private Object value;
    
    public KeyValue() {
    }
    
    public KeyValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
}
