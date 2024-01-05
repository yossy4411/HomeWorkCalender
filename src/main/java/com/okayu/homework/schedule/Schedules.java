package com.okayu.homework.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okayu.homework.schedule.submission.Submission;
import com.okayu.homework.schedule.submission.SubmissionList;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.StreamSupport;

public class Schedules {
    private final JsonNode jsonData;
    private final HashMap<Integer,Schedule> scheduleList = new HashMap<>();
    public Schedules(JsonNode input){
        jsonData = input;
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
        List<Schedule> result = new ArrayList<>();
        for(Map.Entry<Integer,Schedule> node:scheduleList.entrySet()){
            if(node.getValue().isPeriod(time)) result.add(node.getValue());
        }
        result.sort(Comparator.comparingLong(Schedule::getSort));
        return result.stream().mapToInt(Schedule::getId).boxed().toList();
    }
/*    public List<Integer> searchScheduleByStart(LocalDate time) {
        List<Integer> result = new ArrayList<>();
        for(Map.Entry<Integer,Schedule> node:scheduleList.entrySet()){
            if(node.getValue().isStart(time)) result.add(node.getKey());
        }
        return result;
    }*/
    public SubmissionList Submissions() {
        JsonNode node=jsonData.get("regulars");
        return new SubmissionList(StreamSupport.stream(node.spliterator(), false)
                .map(Submission::new)
                .toArray(Submission[]::new));
    }
    public List<Subject> Subjects(){
        JsonNode node=jsonData.get("subjects");
        return StreamSupport.stream(node.spliterator(), false)
                .map(Subject::new)
                .toList();
    }
}
