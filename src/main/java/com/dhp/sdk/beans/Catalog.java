package com.dhp.sdk.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Catalog {
    private Descriptor descriptor;
    private ArrayList<Provider> providers;

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public ArrayList<Provider> getProviders() {
        return providers;
    }

    public void setProviders(ArrayList<Provider> providers) {
        this.providers = providers;
    }

    @Override
    public String toString() {
        return "Catalog{" +
                "descriptor=" + descriptor +
                ", providers=" + providers +
                '}';
    }
}
