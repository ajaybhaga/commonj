/*
 * Created on May 24, 2005
 */
package org.jcommon.lang;

/**
 * @author Matt Hicks
 */
public class KeyValue<K, O> {
    private K key;
    private O value;
    
    public KeyValue() {
    }
    
    public KeyValue(K key, O value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() {
        return key;
    }
    
    public void setKey(K key) {
        this.key = key;
    }
    
    public O getValue() {
        return value;
    }
    
    public void setValue(O value) {
        this.value = value;
    }
}
