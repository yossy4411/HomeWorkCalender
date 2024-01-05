package com.okayu.homework.schedule;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class HWObject {
    private final String id;
    private final String name;
    public HWObject(JsonNode node){
        id = node.get("id").asText();
        name = node.get("name").asText();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public abstract String toString();
}
