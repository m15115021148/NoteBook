package com.geek.springdemo.model;

import java.util.List;

/**
 * 类型 实体类
 * Created by chenmeng on 2017/3/24.
 */

public class KindModel extends ResultModel {
    private List<DateBean> data;

    public List<DateBean> getData() {
        return data;
    }

    public void setData(List<DateBean> data) {
        this.data = data;
    }

    public static class DateBean {

        private String kindID;
        private String kind;

        public String getKindID() {
            return kindID;
        }

        public void setKindID(String kindID) {
            this.kindID = kindID;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }
    }
}
