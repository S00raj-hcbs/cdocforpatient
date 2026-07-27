package com.cdfortis.datainterface;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by diuy on 2014/7/3.
 */
public interface JsonSerializable extends Serializable {
    public void deserialize(JSONObject jsonObject);
    public void serialize(JSONObject jsonObject);
}
