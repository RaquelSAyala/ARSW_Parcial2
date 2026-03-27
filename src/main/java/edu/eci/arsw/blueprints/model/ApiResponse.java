package edu.eci.arsw.blueprints.model;

/**
 * Respuesta estándar para la API REST
 */

public record ApiResponse<T>(int code, String message, T data) { }
