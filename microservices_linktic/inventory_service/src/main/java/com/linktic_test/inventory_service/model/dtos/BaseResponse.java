package com.linktic_test.inventory_service.model.dtos;

/**
 * Base response record for API responses
 */
public record BaseResponse(String[] errorMessages) {
    
    /**
     * Check if the response has errors
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return errorMessages != null && errorMessages.length > 0;
    }
}
