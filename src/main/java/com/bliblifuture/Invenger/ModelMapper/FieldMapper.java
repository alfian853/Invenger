package com.bliblifuture.Invenger.ModelMapper;

public interface FieldMapper<T> {
    void insertValueToObject(T object, String field, String value) throws Exception;
}
