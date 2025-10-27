package com.linktic_test.orders_service.model.dtos;

/**
 * Record que representa una respuesta base del sistema
 * Contiene información sobre errores si los hay
 */
public record BaseResponse(String[] errorMessages) {
    
    /**
     * Método para verificar si la respuesta tiene errores
     * @return true si hay errores, false en caso contrario
     */
    public boolean hasErrors() {
        return errorMessages != null && errorMessages.length > 0;
    }
}
