package com.shinnytech.futures.model.bean.futureinfobean;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenli on 12/26/16.
 */
public class ChartEntity {

    private Map<String, String> state = new HashMap<>();
    private String left_id;
    private String right_id;

    public Map<String, String> getState() {
        return state;
    }

    public void setState(Map<String, String> state) {
        this.state = state;
    }

    public String getLeft_id() {
        return left_id;
    }

    public void setLeft_id(String left_id) {
        this.left_id = left_id;
    }

    public String getRight_id() {
        return right_id;
    }

    public void setRight_id(String right_id) {
        this.right_id = right_id;
    }

    @Override
    public String toString() {
        return "left_id"+left_id+"\n"+"right_id"+right_id+"\n"+
                "ins_list"+state.get("ins_list")+"\n"+"duration"+state.get("duration");
    }
}
