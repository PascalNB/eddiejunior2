package com.pascalnb.eddie.models;

public interface ComponentProvider<T extends EddieComponent> {

    T getComponent();

}
