package com.okayu.homework.schedule.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.okayu.homework.schedule.HWObject;

public class Submission extends HWObject {
    public Submission(JsonNode node) {
        super(node);
    }


    @Override
    public String toString() {
        return getName();
    }
}
