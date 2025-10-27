package com.linktic_test.orders_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utilidad para conversión de objetos a JSON y viceversa
 */
public class JsonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // Registrar el módulo para manejar fechas de Java 8+
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Convierte un objeto a JSON string
     * @param object el objeto a convertir
     * @return JSON string
     * @throws RuntimeException si hay error en la conversión
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    /**
     * Convierte un JSON string a objeto de la clase especificada
     * @param json el JSON string
     * @param clazz la clase del objeto destino
     * @param <T> el tipo del objeto
     * @return el objeto convertido
     * @throws RuntimeException si hay error en la conversión
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to object", e);
        }
    }
}
