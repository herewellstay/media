package com.herewellstay.events;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {
    private String name;
    private Map<String, Object> data=new HashMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Object get(String key){
        return data.get(key);
    }
    public Object put(String key, Object value){
        return data.put(key, value);
    }
}



