package com.okayu.homework.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedules {
    private final HashMap<Integer,Schedule> scheduleList = new HashMap<>();
    public Schedules(JsonNode input){
        JsonNode nodes = input.get("schedules");
        if(nodes.isArray()) {
            for (JsonNode schedule : nodes) {
                Schedule a = new Schedule(schedule);
                scheduleList.put(a.getId(), a);
            }
        }
    }
    public Schedules (String filepath) throws IOException {
        this(new ObjectMapper().readTree(new File(filepath)));
    }

    public HashMap<Integer, Schedule> getScheduleList() {
        return scheduleList;
    }
    public List<Integer> searchSchedule(LocalDate time) {
        List<Integer> result = new ArrayList<>();
        for(Map.Entry<Integer,Schedule> node:scheduleList.entrySet()){
            if(node.getValue().isPeriod(time)) result.add(node.getKey());
        }
        return result;
    }
    public List<Integer> searchScheduleByStart(LocalDate time) {
        List<Integer> result = new ArrayList<>();
        for(Map.Entry<Integer,Schedule> node:scheduleList.entrySet()){
            if(node.getValue().isStart(time)) result.add(node.getKey());
        }
        return result;
    }

}
