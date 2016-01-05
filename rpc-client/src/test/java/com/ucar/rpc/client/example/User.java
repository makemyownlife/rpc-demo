package com.ucar.rpc.client.example;

import java.io.Serializable;

/**
 * Created by zhangyong on 16/1/4.
 */
public class User implements Serializable {

    private static final long serialVersionUID = -5565366231695911316L;

    private String username;

    private int a;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

}
