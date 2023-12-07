package com.okayu.homework;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import one.cafebabe.bc4j.BusinessCalendar;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Objects;

public class Controller {
    @FXML
    private VBox schedule;
    @FXML
    private GridPane calender;
    private final String systemFile = System.getProperty("user.home")+File.separator+"HWCalender"+File.separator;
    @FXML
    private SVGPath right;
    @FXML
    private SVGPath left;
    @FXML
    private Label month;
    private JsonNode jsonData;
    private YearMonth time;

    public void initialize() {
        jsonData = readSchedule(systemFile+"schedules.json");
        ZoneId zoneId = ZoneId.of("Asia/Tokyo");
        time = YearMonth.now(zoneId);
        setCalender(time);

        right.setOnMouseClicked((mouseEvent)-> {
            time = time.plusMonths(1);
            setCalender(time);
        });
        left.setOnMouseClicked((mouseEvent)-> {
            time = time.plusMonths(-1);
            setCalender(time);
        });
        calender.setOnScroll((scrollEvent)->{
            time = scrollEvent.getDeltaY() > 0 ? time.plusMonths(-1) : time.plusMonths(1);
            setCalender(time);
        });


    }

    private JsonNode readSchedule(String filepath) {
        try {
            File file = new File(filepath);
            return new ObjectMapper().readTree(file);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    private void setCalender(YearMonth month){
        BusinessCalendar holiday = BusinessCalendar.newBuilder()
                .holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS)
                .build();
        calender.getChildren().clear();
        LocalDate preset = LocalDate.of(month.getYear(),month.getMonthValue(),1);
        int first = preset.getDayOfWeek().getValue()%7;
        LocalDate date=preset.minusDays(first);
        for(int i= 0;i<35;i++) {
            int[] index = {i%7, i/7};

            Rectangle rectangle = new Rectangle(calender.getPrefWidth()/7,calender.getPrefHeight()/5);
            if(i<first||i-first>=preset.lengthOfMonth()) rectangle.getStyleClass().add("otherMonth"); else rectangle.getStyleClass().add("background");
            LocalDate finalDate = date;
            rectangle.setOnMouseClicked((mouseEvent)->setDetail(finalDate));
            Group group = new Group(rectangle);
            Label label = new Label(date.getDayOfMonth() + "日");
            label.getStyleClass().add("day");

            label.setLayoutX(2);
            label.setLayoutY(1);
            if(date.getDayOfWeek()== DayOfWeek.SATURDAY) label.setTextFill(Color.BLUE);
            if(holiday.isHoliday(date)||date.getDayOfWeek()==DayOfWeek.SUNDAY) {
                label.setTextFill(Color.RED);
                if (holiday.isHoliday(date)) {
                    Label schedule = new Label(Objects.requireNonNull(holiday.getHoliday(date)).name.replace("japanese.",""));
                    schedule.getStyleClass().add("schedule");
                    schedule.setLabelFor(rectangle);
                    schedule.setMaxWidth(calender.getPrefWidth()/7);
                    schedule.setLayoutY(11);
                    schedule.setLayoutX(2);
                    group.getChildren().add(schedule);

                }
            }
            group.getChildren().add(label);
            calender.add(group, index[0],index[1]);
            date=date.plusDays(1);
        }
        this.month.setText(month.getYear()+"年"+month.getMonthValue()+"月");
    }

    private void setDetail(LocalDate date) {
        schedule.getChildren().clear();
        var scheduleNode = jsonData.at("/schedules/"+ date.getYear()+"/"+date.getMonth().getValue()+"/"+date.getDayOfMonth());
        for(JsonNode node:scheduleNode){
            if (node.at("/type").asText().equals("homework")){
                Pane pane = new Pane();
                Label subject = new Label(node.at("/subject").asText());
                Label title = new Label(node.at("/title").asText());
                subject.setFont(new Font(8));
                title.setFont(new Font(15));
                HBox hBox = new HBox(subject,title);
                hBox.setAlignment(Pos.CENTER);
                hBox.setLayoutX(5);
                pane.getChildren().add(hBox);
                pane.getStyleClass().add("schedule");
                pane.setStyle("-fx-background-color: "+node.at("/color")+";-fx-background-radius: 3;");
                title.setLayoutX(3);
                title.setLayoutY(1);
                schedule.getChildren().add(pane);
            }
        }

    }
//    private void addDetailObject(String title,Color background,String description,String[] other){
//
//    }
}