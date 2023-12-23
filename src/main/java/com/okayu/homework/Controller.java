package com.okayu.homework;

import com.okayu.homework.schedule.Schedule;
import com.okayu.homework.schedule.Schedules;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import one.cafebabe.bc4j.BusinessCalendar;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

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
    @FXML private VBox menu;
    //@FXML private ScrollPane menubar;
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
            int nextIncludes = -1;
            int nextEquals = 0;
            for (int j = 0; j < list.size(); j++) {
                int systemIndex = Math.min(j, 4);
                int i2 = list.get(j);
                Schedule node = jsonData.getScheduleList().get(i2);
                Polyline line = new Polyline();
                line.setStroke(node.getColor());
                var paths = line.getPoints();
                paths.addAll(0.0, 18 + 6.0 * systemIndex);
                if (nextDay.contains(i2)) {
                    nextIncludes = j;
                    if (date.getDayOfWeek() == DayOfWeek.SATURDAY)
                        paths.addAll(calender.getPrefWidth() / 7, 18 + 6.0 * systemIndex);
                    else {
                        int nextIndex = Math.min(nextDay.indexOf(i2), 4);
                        if (nextIndex <= systemIndex) {
                            paths.addAll(calender.getPrefWidth() / 7, 18 + 6.0 * nextIndex);
                            nextEquals = j + 1;
                            if(nextIndex==systemIndex&&systemIndex==4) line.setStroke(Color.GRAY);
                        } else paths.addAll(calender.getPrefWidth() / 7 - 3 - 6 * (j - nextEquals), 18 + 6.0 * j,
                                calender.getPrefWidth() / 7 - 3 - 6 * (j - nextEquals), 18 + 6.0 * nextIndex,
                                calender.getPrefWidth() / 7, 18 + 6.0 * nextIndex);
                    }
                } else {
                    paths.addAll(calender.getPrefWidth() / 7,
                            18 + 6.0 * (j == list.size() - 1 && nextDay.size() != list.size() ? nextDay.size() : nextIncludes + 1));
                    if(systemIndex==4) line.setStroke(Color.GRAY);
                }

                line.setStrokeLineCap(StrokeLineCap.BUTT);
                line.setStrokeLineJoin(StrokeLineJoin.ROUND);
                line.setStrokeWidth(6);
                group.getChildren().add(line);
                if (j > 4) break;
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
            AnchorPane pane = new AnchorPane();
            Label subject = new Label("休日");
            Label title = new Label(Objects.requireNonNull(holiday.getHoliday(date)).name.replace("japanese.","").replace("休日","国民の休日"));
            if(holiday.isHoliday(date.minusDays(1))&&date.getDayOfWeek()==DayOfWeek.MONDAY&&title.getText().equals("国民の休日")) title.setText("振替休日");
            subject.getStyleClass().add("subject");
            subject.setTextFill(Color.WHITE);
            title.setTextFill(Color.WHITE);
            title.getStyleClass().add("title");
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
            AnchorPane pane = new AnchorPane();
            Label subject = new Label(node.getSubject());
            Label title = new Label(node.getTitle());
            subject.getStyleClass().add("subject");
            title.getStyleClass().add("title");
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
        StackPane pane = new StackPane();
        Label subject = new Label("予定を追加");
        subject.setFont(new Font(11));
        pane.getChildren().add(subject);
        pane.setAlignment(Pos.CENTER);
        pane.getStyleClass().add("newSchedule");
        pane.setLayoutY(20);
        pane.setOnMouseClicked((mouseEvent -> addNewSchedule(date)));
        menu.getChildren().clear();
        menu.getChildren().add(pane);

    }

    private void addNewSchedule(LocalDate date) {
        Tab tab = new Tab();
        tab.setClosable(true);
        int id = 0;
        for (Tab tab1 : scheduleTab.getTabs()) {
            if (tab1.getId().contains("new_")){
                int i = Math.max(Integer.parseInt(tab1.getId().replace("new_", "")), id);
                if (tab1.getId().contains("new_")) id = i;
            }
        }
        id++;
        Label header = new Label("新しい予定"+(id==1?"":" "+id));
        header.setStyle("-fx-font-size:10");
        tab.setId("new_"+id);
        header.setMaxWidth(50);
        Icon close = new Icon("close",15);
        close.setOnMouseClicked(mouseEvent->scheduleTab.getTabs().remove(tab));
        tab.setGraphic(new HBox(header, close));
        VBox index = new VBox();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String[][] configs = new String[][]{{"タイトル","新規予定"+id,"予定のタイトル"},
                {"開始時間",date.format(Schedule.dateFormatter),date.format(dateTimeFormatter)},
                {"終了時間",date.format(Schedule.dateFormatter),date.format(dateTimeFormatter)},
        };
        var inputs = new Object[configs.length];
        for (int i = 0; i < configs.length; i++) {
            String[] config = configs[i];
            addContext(config, index, inputs, i);
            if(i==2){
                CheckBox checkBox = new CheckBox();
                ComboBox<Integer> comboBox = new ComboBox<>();
                comboBox.setVisibleRowCount(5);
                comboBox.getItems().addAll(IntStream.rangeClosed(0, 23).boxed().toList());
                comboBox.setDisable(true);
                ComboBox<Integer> comboBox2 = new ComboBox<>();
                comboBox2.setVisibleRowCount(5);
                comboBox2.getItems().addAll(IntStream.rangeClosed(0, 60).boxed().toList());
                comboBox2.setDisable(true);
                HBox splitPane = new HBox(checkBox, comboBox, comboBox2);
                splitPane.setAlignment(Pos.CENTER);
                checkBox.selectedProperty().addListener((a,oldV,newV)-> {
                    comboBox2.setDisable(!newV);
                    comboBox.setDisable(!newV);
                });
                index.getChildren().add(splitPane);
            }
        }
        /*for (Node node:inputs){
            if(node instanceof TextField input){
                input.getText();
            }else if(node instanceof TextArea input){

            }
        }*/
        tab.setContent(new ScrollPane(index));
        scheduleTab.getTabs().add(tab);
    }
    private void addContext(String[] config, Pane node, Object[] c, int index){
        Label label = new Label(config[0]);
        if(index==1|index==2){
            DatePicker field = new DatePicker();
            try {
                field.setPromptText(config[2]);
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }

            label.setMinWidth(120);
            field.setMinWidth(180);
            HBox splitPane = new HBox(label, field);
            splitPane.setAlignment(Pos.CENTER);
            c[index] = field.getValue();
            node.getChildren().add(splitPane);
        }else {
            var field = new TextField();
            try {
                field.setPromptText(config[2]);
            } catch (ArrayIndexOutOfBoundsException ignored) {}
            label.setMinWidth(120);
            field.setMinWidth(180);
            HBox splitPane = new HBox(label, field);
            splitPane.setAlignment(Pos.CENTER);
            c[index] = field.getText();
            node.getChildren().add(splitPane);
        }
    }
    private void addTab(String title, int id){
        Tab tab = new Tab();
        tab.setClosable(true);
        tab.setId("sc"+id);
        Label header = new Label(title);
        header.setMaxWidth(50);
        header.setStyle("-fx-font-size:10");
        Icon close = new Icon("close",15);
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