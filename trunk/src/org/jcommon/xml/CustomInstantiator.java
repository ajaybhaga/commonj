/**
 * Created: Feb 12, 2007
 */
package org.jcommon.xml;

import org.jdom.*;

/**
 * @author Matt Hicks
 *
 */

public interface CustomInstantiator {
	public abstract Object instantiate(Element element);
}
