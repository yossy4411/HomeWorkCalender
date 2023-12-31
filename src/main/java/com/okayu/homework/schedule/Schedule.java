package com.okayu.homework.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Schedule {
    private LocalDateTime start;
    private final JsonNode node;
    private LocalDateTime finish;
    private final String title;
    private final String subject;
    private final int id;
    private final Color color;
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public Schedule(JsonNode node){

        if(node.get("type").asText().equals("homework")){
            start = LocalDate.parse(node.get("distribute").asText(), dateFormatter).atStartOfDay();
            finish = LocalDate.parse(node.get("submit").asText(),dateFormatter).atStartOfDay();
            subject = node.get("subject").asText();
        }else {
            try {
                start = LocalDate.parse(node.get("start").asText(), dateFormatter).atStartOfDay();

            }catch (DateTimeParseException e){
                start = LocalDateTime.parse(node.get("start").asText(),dateTimeFormatter);
            }
            try {
                finish = LocalDate.parse(node.get("end").asText(), dateFormatter).atStartOfDay();
            }catch (DateTimeParseException e){
                finish = LocalDateTime.parse(node.get("end").asText(),dateTimeFormatter);
            }
            subject = "予定";
        }
        title = node.get("title").asText();
        color = Color.valueOf(node.get("color").asText());
        id  = node.get("id").asInt();
        this.node = node;
    }

    public long getSort(){return start.toEpochSecond(ZoneOffset.ofHours(9));}
    public LocalDateTime getFinish(){return finish;}
    public String getTitle(){return title;}
    public Color getColor(){return color;}
    public String getColorCode(){
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public String getSubject(){
        return subject;
    }

    public int getId(){return id;}
    public boolean isPeriod(LocalDate date){
        return !date.isBefore(start.toLocalDate()) && !date.isAfter(finish.toLocalDate());
    }
    public boolean isPeriod(LocalDateTime date){
        return start.isAfter(date)&&finish.isBefore(date);
    }
    public boolean isStart(LocalDateTime date){
        return  start.isEqual(date);
    }
    public boolean isFinish(LocalDateTime date){
        return finish.isEqual(date);
    }
    public boolean isStart(LocalDate date){
        return  start.toLocalDate().isEqual(date);
    }
    public boolean isFinish(LocalDate date){
        return finish.toLocalDate().isEqual(date);
    }
    public static class ScheduleBuilder{
        String title;
        ScheduleType type;
        LocalDate start;
        LocalTime startTime;
        public ScheduleBuilder(){
        }
        public ScheduleBuilder name(String title){
            this.title = title;
            return this;
        }
        public ScheduleBuilder type(ScheduleType type){
            this.type = type;
            return this;
        }
        public ScheduleBuilder start(LocalDate start){
            this.start = start;
            return this;
        }
        public ScheduleBuilder start(LocalDateTime start){
            this.start = start.toLocalDate();
            this.startTime = start.toLocalTime();
            return this;
        }
        public ScheduleBuilder time(LocalTime startTime){
            this.startTime = startTime;
            return this;
        }
        public Schedule build() {
            ObjectNode jsonNodes = new ObjectMapper().createObjectNode();
            if (type.equals(ScheduleType.Homework)){
                jsonNodes.put("type",type.asText());
                jsonNodes.put("provided", "");
                jsonNodes.put("share", ShareLevel.None.asText());
            }
            return new Schedule(jsonNodes);
        }
    }
}
