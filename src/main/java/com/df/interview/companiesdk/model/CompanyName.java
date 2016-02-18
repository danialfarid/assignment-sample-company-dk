package com.df.interview.companiesdk.model;

import java.io.Serializable;

public class CompanyName implements Serializable {
    private Long id;
    private String name;

    public CompanyName() {
    }

    public CompanyName(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
