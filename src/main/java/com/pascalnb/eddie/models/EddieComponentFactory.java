package com.pascalnb.eddie.models;

import java.util.function.Function;

public interface EddieComponentFactory<T extends EddieComponent> extends Function<ComponentConfig, T> {

}
