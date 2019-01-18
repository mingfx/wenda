package com.nowcoder.wenda.model;


import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Feed {
    private int id;
    private int type;
    private int userId;
    private Date createdDate;
    //JSON可以存储各种字段
    private String data;
    //
    private JSONObject dataJSON = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        //当传入data时吧JSON初始化
        dataJSON = JSONObject.parseObject(data);
    }

    //get方法，可以获取dataJSON的数据，有了这个get方法，在html中就可以直接通过.来获取属性值
    public String get(String key){
        return dataJSON==null?null:dataJSON.getString(key);
    }
}
