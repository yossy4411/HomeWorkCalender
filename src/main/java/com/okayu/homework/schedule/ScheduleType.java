package com.okayu.homework.schedule;

public class ScheduleType {
    private final String type;
    private ScheduleType(String type){
        this.type = type;
    }
    public static ScheduleType Homework = new ScheduleType("homework");

    public boolean equals(ScheduleType object){
        return type.equals(object.type);
    }

    @Override
    public String toString() {
        return "ScheduleType{" +
                "type='" + type + '\'' +
                '}';
    }

    public String asText() {
        return type;
    }

}
