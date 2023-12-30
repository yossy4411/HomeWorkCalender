package com.okayu.homework;

import com.okayu.homework.schedule.Schedule;
import com.okayu.homework.schedule.Schedules;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.converter.LocalTimeStringConverter;
import one.cafebabe.bc4j.BusinessCalendar;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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

            LocalDate finalDate = date;

            Pane group = new Pane();
            group.setPrefWidth(calender.getPrefWidth()/7.0-1);
            if(i<first||i-first>=preset.lengthOfMonth()) group.getStyleClass().add("otherMonth"); else group.getStyleClass().add("background");
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
        Button sub = new Button("予定を追加");
        sub.setPrefWidth(450);
        sub.setStyle("-fx-background-color: d6d6d6; -fx-background-radius:4 ");
        sub.setOnAction(event -> addNewSchedule(date));
        menu.getChildren().clear();
        menu.getChildren().add(sub);

    }

    private void addNewSchedule(LocalDate date) {
        int id = 0;
        for (Tab tab1 : scheduleTab.getTabs()) {
            if (tab1.getId().contains("new_")){
                int i = Math.max(Integer.parseInt(tab1.getId().replace("new_", "")), id);
                if (tab1.getId().contains("new_")) id = i;
            }
        }
        id++;
        Tab tab = new Tab("新しい予定"+(id==1?"":" "+id));
        Label header = new Label();
        header.setStyle("-fx-font-size:10");
        tab.setId("new_"+id);
        header.setMaxWidth(50);
        VBox index = new VBox();
        index.setSpacing(4);
        {
            GridPane content = new GridPane();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String[][] configs = new String[][]{{"タイトル", "新規予定" + id, "予定のタイトル"},
                    {"開始時間", date.format(Schedule.dateFormatter), date.format(dateTimeFormatter)},
                    {"終了時間", date.format(Schedule.dateFormatter), date.format(dateTimeFormatter)},
                    {"予定の色", "", "カレンダー上で表示される色"},
                    {"説明", "", "詳細で表示されるテキスト"}
            };

            content.setMaxWidth(355);
            content.setPadding(new Insets(2));
            content.getColumnConstraints().addAll(new ColumnConstraints(90),new ColumnConstraints(240));
            content.setVgap(3);
            var inputs = new Object[configs.length];
            for (int i = 0; i < configs.length; i++) {
                String[] config = configs[i];
                addContext(config, content, inputs, i);
            }
            index.getChildren().add(content);
        }
        {
            Accordion content = new Accordion();
            for (int i = 0; i<2; i++) {
                newSubmission(content);
            }
            index.getChildren().add(content);
            Button newPt = new Button("新規提出物");
            newPt.setOnAction(event->newSubmission(content));
            index.getChildren().add(newPt);
        }
        ScrollPane contents = new ScrollPane(index);
        tab.setContent(contents);
        scheduleTab.getTabs().add(tab);
    }
    private void newSubmission(Accordion content){
        GridPane cont = new GridPane();
        TitledPane titledPane = new TitledPane("提出物",cont);
        titledPane.setAnimated(true);
        {
            cont.add(new Label("教科"), 0, 0);
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().add("英語");
            comboBox.setEditable(true);
            cont.add(comboBox, 1, 0);
            cont.add(new Label("教科"), 0, 0);

        }
        {
            cont.add(new Label("タイトル"), 0, 1);
            System.out.println(content.getAccessibleText());
            TextField input = new TextField("提出物");
            input.textProperty().addListener((observable, oldValue, newValue) -> titledPane.setText(newValue));
            cont.add(input, 1, 1);
        }
        content.getPanes().add(titledPane);
    }
    private void addContext(String[] config, GridPane node, Object[] c, int index){
        Label label = new Label(config[0]);
        label.setLayoutX(4);
        if(index==1|index==2){
            DatePicker field = new DatePicker();

            field.valueProperty().addListener((a,b,f)->System.out.println(f.toString()));
            try {
                field.setPromptText(config[2]);
            } catch (ArrayIndexOutOfBoundsException ignored) {}
            CheckBox checkBox = new CheckBox();
            TextField textField = new TextField();
            textField.setPromptText(LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
            textField.setTextFormatter(new TextFormatter<>(new LocalTimeStringConverter(FormatStyle.SHORT)));
            textField.setDisable(true);
            checkBox.selectedProperty().addListener((a,oldV,newV)-> textField.setDisable(!newV));
            field.setPrefWidth(220);
            HBox splitPane = new HBox(field,checkBox, textField);
            node.addRow(index,label,splitPane);
            splitPane.setSpacing(5);
            splitPane.setAlignment(Pos.BASELINE_LEFT);
            c[index] = field.getValue();
        }else if(index==3){
            var field = new ColorPicker(Color.WHITE);
            try {
                field.setPromptText(config[2]);
            } catch (ArrayIndexOutOfBoundsException ignored) {}
            field.setMinWidth(220);
            c[index] = field.getValue();
            node.addRow(index,label,field);
        } else{
            var field = index==4?new TextArea(config[1]):new TextField(config[1]);
            try {
                field.setPromptText(config[2]);
            } catch (ArrayIndexOutOfBoundsException ignored) {}
            c[index] = field.getText();
            node.addRow(index,label,field);
        }
    }
    private void addTab(String title, int id){
        Tab tab = new Tab(title);
        tab.setId("sc"+id);
        VBox body = new VBox();
        tab.setContent(body);
        if(scheduleTab.getTabs().stream()
                .noneMatch(paneTab -> paneTab.getId().equals("sc"+id)))scheduleTab.getTabs().add(tab);
        tab.getStyleClass().add("scheduleTab");
        tab.setStyle("-fx-background:"+jsonData.getScheduleList().get(id).getColorCode());
    }


}