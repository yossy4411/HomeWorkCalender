package com.okayu.homework.schedule;

import java.util.Arrays;
import java.util.stream.Collectors;

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
    public static class ShareLevel{
        private final String type;
        private ShareLevel(String type){
            this.type = type;
        }

        /**
         *
         * IDから公開対象を自動選択します。
         * @param codes SchoolCode | UserID | AreaCode | ClassNumber
         */
        public ShareLevel(int[] codes){
            this(Arrays.stream(codes)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining(":")));
        }

        public static ShareLevel None = new ShareLevel("false");
        public static ShareLevel Friend = new ShareLevel("friend");
        public static ShareLevel Class = new ShareLevel("classmate");
        public static ShareLevel Grade = new ShareLevel("grade");
        public static ShareLevel School = new ShareLevel("school");
        public static ShareLevel Pref = new ShareLevel("area");
        public static ShareLevel All = new ShareLevel("all");
        public boolean equals(ScheduleType object){
            return type.equals(object.type);
        }
        public String asText(){
            return type;
        }
    }
}
