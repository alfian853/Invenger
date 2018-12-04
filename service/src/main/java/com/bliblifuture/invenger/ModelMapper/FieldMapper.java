package com.bliblifuture.invenger.ModelMapper;

public interface FieldMapper<T> {
    void insertValueToObject(T object, String field, String value) throws Exception;
}
