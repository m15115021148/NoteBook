package com.geek.springdemo.model;

import java.util.List;

/**
 * Created by Administrator on 2017/4/17.
 */

public class LineModel {
    private String kind;
    private List<TimeModel> value;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<TimeModel> getValue() {
        return value;
    }

    public void setValue(List<TimeModel> value) {
        this.value = value;
    }

    public static class TimeModel {
        private String time;
        private String money;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
