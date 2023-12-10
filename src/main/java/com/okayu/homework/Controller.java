package com.okayu.homework;

import com.okayu.homework.schedule.Schedule;
import com.okayu.homework.schedule.Schedules;
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
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
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
import java.util.List;
import java.util.Locale;
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
    private Schedules jsonData;
    private YearMonth time;
    private final BusinessCalendar holiday = BusinessCalendar.newBuilder()
            .holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS)
            .on(2023,12,2).holiday("アプリ開発開始日")
            .build();

    public void initialize() {
        try{
            jsonData = new Schedules(systemFile+"schedules.json");
        }catch(IOException e){
            e.printStackTrace();
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

    private void setCalender(YearMonth month){
        calender.getChildren().clear();
        LocalDate preset = LocalDate.of(month.getYear(),month.getMonthValue(),1);
        int first = preset.getDayOfWeek().getValue()%7;
        LocalDate date=preset.minusDays(first);
        HashMap<Integer, Polyline> scheduleLine = new HashMap<>();
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
                    Label schedule = new Label(Objects.requireNonNull(holiday.getHoliday(date)).name.replace("japanese.","").replace("休日","国民の休日"));
                    if(holiday.isHoliday(date.minusDays(1))&&date.getDayOfWeek()==DayOfWeek.MONDAY&&schedule.getText().equals("国民の休日")) schedule.setText("振替休日");
                    schedule.getStyleClass().add("schedule");
                    schedule.setLabelFor(rectangle);
                    schedule.setMaxWidth(calender.getPrefWidth()/7-25);
                    schedule.setLayoutY(1);
                    schedule.setLayoutX(25);
                    group.getChildren().add(schedule);

                }
            }
            List<Integer> list = jsonData.searchSchedule(date);
            List<Integer> nextDay = jsonData.searchSchedule(date.plusDays(1));
            for (int j = 0; j < list.size(); j++) {
                int i2 = list.get(j);
                Schedule node = jsonData.getScheduleList().get(i2);
                Polyline line = new Polyline();
                var paths = line.getPoints();
                paths.addAll(0.0, 18+6.0*j);
                if(nextDay.contains(i2)&&nextDay.indexOf(i2)<j){
                    paths.addAll(calender.getPrefWidth() / 7, 18 + 6.0 * nextDay.indexOf(i2));
                }else {
                    paths.addAll(calender.getPrefWidth() / 7, 18 + 6.0 * j);
                }
                line.setStroke(node.getColor());
                line.setStrokeLineCap(StrokeLineCap.BUTT);
                line.setStrokeWidth(6);
                group.getChildren().add(line);
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
        List<Integer> schedules = jsonData.searchSchedule(date);
        if(holiday.isHoliday(date)){
            Pane pane = new Pane();
            Label subject = new Label("休日");
            Label title = new Label(Objects.requireNonNull(holiday.getHoliday(date)).name.replace("japanese.","").replace("休日","国民の休日"));
            if(holiday.isHoliday(date.minusDays(1))&&date.getDayOfWeek()==DayOfWeek.MONDAY&&title.getText().equals("国民の休日")) title.setText("振替休日");
            subject.setFont(new Font(8));
            subject.setTextFill(Color.WHITE);
            title.setFont(new Font(15));
            title.setTextFill(Color.WHITE);
            HBox hBox = new HBox(subject, title);
            hBox.setSpacing(4);
            hBox.setAlignment(Pos.CENTER);
            hBox.setLayoutX(5);
            pane.getChildren().add(hBox);
            pane.getStyleClass().add("schedule");
            pane.setStyle("-fx-background-color:red;-fx-background-radius: 3;");
            title.setLayoutX(3);
            title.setLayoutY(1);
            schedule.getChildren().add(pane);
        }
        for(int id: schedules){
            Schedule node = jsonData.getScheduleList().get(id);
            Pane pane = new Pane();
            Label subject = new Label(node.getSubject());
            Label title = new Label(node.getTitle());
            subject.setFont(new Font(8));
            title.setFont(new Font(15));
            HBox hBox = new HBox(subject, title);
            hBox.setAlignment(Pos.CENTER);
            hBox.setLayoutX(5);
            pane.getChildren().add(hBox);
            pane.getStyleClass().add("schedule");
            pane.setStyle("-fx-background-color: " + node.getColorCode() + ";-fx-background-radius: 3;");
            pane.setOnMouseClicked((mouseEvent -> addTab(node.getTitle(), id)));
            title.setLayoutX(3);
            title.setLayoutY(1);
            schedule.getChildren().add(pane);
        }

    }

    private void addTab(String title, int id){
        Tab tab = new Tab();
        tab.setClosable(true);
        tab.setId("sc"+id);
        Label header = new Label(title);
        header.setMaxWidth(50);
        Icon close = new Icon("close",18);
        close.setOnMouseClicked(mouseEvent->scheduleTab.getTabs().remove(tab));
        tab.setGraphic(new HBox(header, close));
        VBox body = new VBox();
        tab.setContent(body);
        if(scheduleTab.getTabs().stream()
                .noneMatch(paneTab -> paneTab.getId().equals("sc"+id)))scheduleTab.getTabs().add(tab);
        tab.getStyleClass().add("scheduleTab");
        tab.setStyle("-fx-background:"+jsonData.getScheduleList().get(id).getColorCode());
    }


}