/**
 * 
 */
package org.moxhu.web.app.exception;

import org.moxhu.exception.GeneralException;

/**
 * @author Jesus
 *
 */
public class UnauthorizedRequestException extends GeneralException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1315875763167495987L;

	/**
     * Constructor
     * @param str    a string that explains what the exception condition is
     */
    public UnauthorizedRequestException (String str) {
        super(str);
    } 

    /**
     * Default constructor. Takes no arguments
     */
    public UnauthorizedRequestException () {
        super();
    }

}
