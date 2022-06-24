package com.brenner.investments.data.bulkloader;

/**
 * Errors with the bulk data processing
 * 
 * @author dbrenner
 *
 */
public class BulkDataParseException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    BulkDataParseException() {
        super();
    }
    
    BulkDataParseException(String message) {
        super(message);
    }
    
    BulkDataParseException(String message, Exception e) {
        super(message, e);
    }

}
