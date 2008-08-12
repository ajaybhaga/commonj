/*
 * Created on Oct 15, 2004
 */
package org.jcommon.swing.table;

import java.util.*;

/**
 * @author Matt Hicks
 */
public interface BeanChangeListener extends EventListener {
    public void beanChanged(BeanChangeEvent evt);
}
