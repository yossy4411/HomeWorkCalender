package com.okayu.homework;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Controller {
    @FXML
    private VBox schedule;
    @FXML
    private GridPane calender;
    private final String systemFile = System.getProperty("user.home")+File.separator+"HWCalender"+File.separator;
    private final Locale locale = Locale.getDefault();
    @FXML private SVGPath right;
    @FXML private SVGPath left;
    @FXML private Label month;
    @FXML private TabPane scheduleTab;
    private HashMap<Integer, JsonNode> jsonData;
    private YearMonth time;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void initialize() {
        JsonNode jsonNode = readSchedule(systemFile+"schedules.json");
        if (jsonNode!=null) {
            jsonData = loadJson(jsonNode);
        }

        time = YearMonth.now();
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

    private HashMap<Integer, JsonNode> loadJson(JsonNode node) {

        if(node.has("schedules")){
            HashMap<Integer,JsonNode> hashMap = new HashMap<>();
            JsonNode schedules = node.at("/schedules");
            if(schedules.isArray())for(JsonNode schedule:schedules)hashMap.put(schedule.has("id")?schedule.at("/id").asInt():-1, schedule);
            return hashMap;
        }else{
            return new HashMap<>(Map.of(node.has("id")?node.at("id").asInt():-1, node));
        }

    }

    private JsonNode readSchedule(String filepath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(filepath);
            return objectMapper.readTree(file);
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

            Pane group = new Pane(rectangle);
            group.setOnMouseClicked((mouseEvent)->setDetail(finalDate));
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
        var dateLabel = new Label(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale))+" の予定");
        schedule.getChildren().add(dateLabel);
        for(Map.Entry<Integer, JsonNode> map:jsonData.entrySet()){
            JsonNode node = map.getValue();
            if (node.at("/type").asText().equals("homework")){
                final boolean timerange;
                {
                    LocalDate before = LocalDate.parse(node.at("/distribute").asText(), dateFormatter);
                    LocalDate after = LocalDate.parse(node.at("/submit").asText(), dateFormatter);
                    timerange = !date.isBefore(before)&&!date.isAfter(after);
                }
                if (timerange){
                    Pane pane = new Pane();
                    Label subject = new Label(node.at("/subject").asText());
                    Label title = new Label(node.at("/title").asText());
                    subject.setFont(new Font(8));
                    title.setFont(new Font(15));
                    HBox hBox = new HBox(subject, title);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setLayoutX(5);
                    pane.getChildren().add(hBox);
                    pane.getStyleClass().add("schedule");
                    pane.setStyle("-fx-background-color: " + node.at("/color") + ";-fx-background-radius: 3;");
                    pane.setOnMouseClicked((mouseEvent -> addDetailObject(node.at("/title").asText(), map.getKey())));
                    title.setLayoutX(3);
                    title.setLayoutY(1);
                    schedule.getChildren().add(pane);
                }
            }
        }

    }

    private void addDetailObject(String title, int id){
        Tab tab = new Tab(title);
        tab.setClosable(true);
        tab.setId("sc"+id);
        if(scheduleTab.getTabs().stream()
                .noneMatch(paneTab -> paneTab.getId().equals("sc"+id)))scheduleTab.getTabs().add(tab);

        JsonNode jsonNode = jsonData.get(id);
        tab.getStyleClass().add("scheduleTab");
        tab.setStyle("-fx-background:"+jsonNode.at("/color"));
    }
}