package com.bliblifuture.invenger;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PostgreArrayType implements UserType {

    private final int[] arrayTypes = new int[]{Types.ARRAY};

    @Override
    public int[] sqlTypes() {
        return arrayTypes;
    }

    @Override
    public Class<List> returnedClass() {
        return List.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == null ? y == null : x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public List<Integer> nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        if (rs != null && rs.getArray(names[0]) != null) {
            Object array = rs.getArray(names[0]).getArray();
            if (array instanceof Integer[]){

                List<Integer> res = Arrays.asList((Integer[]) array);
                if(res.get(0) == null){
                    return new LinkedList<Integer>();
                }
                return res;
            }
            else
                throw new HibernateException(names[0]+" is not Integer[]");
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        throw new HibernateException("nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) not implemented yet");
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        throw new HibernateException("deepCopy(Object value) not implemented yet");
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        throw new HibernateException("disassemble(Object value) not implemented yet");
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        throw new HibernateException("assemble(Serializable cached, Object owner) not implemented yet");
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        throw new HibernateException("replace(Object original, Object target, Object owner) not implemented yet");
    }
}
