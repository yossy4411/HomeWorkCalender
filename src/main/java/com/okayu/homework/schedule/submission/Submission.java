package com.okayu.homework.schedule.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.okayu.homework.schedule.HWObject;

public class Submission extends HWObject {
    private final int pages;
    public Submission(JsonNode node) {
        super(node);
        pages = node.get("pages").asInt();
    }


    @Override
    public String toString() {
        return getName();
    }

    public int getPages() {
        return pages;
    }
}
