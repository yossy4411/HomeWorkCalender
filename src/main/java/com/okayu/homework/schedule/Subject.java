package com.okayu.homework.schedule;

import com.fasterxml.jackson.databind.JsonNode;

public class Subject extends HWObject{

    public Subject(JsonNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return getName();
    }
}
