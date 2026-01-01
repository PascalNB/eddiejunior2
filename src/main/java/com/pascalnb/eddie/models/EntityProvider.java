package com.pascalnb.eddie.models;

public interface EntityProvider<T>{

    T getEntity();

    Class<T> getEntityType();

}
