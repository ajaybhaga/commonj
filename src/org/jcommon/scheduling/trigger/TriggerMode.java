/**
 * Created: Mar 29, 2007
 */
package org.jcommon.scheduling.trigger;

/**
 * @author Matt Hicks
 *
 */
public enum TriggerMode {
	 /** Offset for next invocation is based off the beginning of previous execution
	 */
	EXECUTION_OFFSET,
	/**
	 * Offset for next invocation is based off the completion of previous execution
	 */
	COMPLETION_OFFSET
}
