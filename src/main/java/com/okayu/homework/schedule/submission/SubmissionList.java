package com.okayu.homework.schedule.submission;

import java.util.ArrayList;
import java.util.List;

public class SubmissionList extends ArrayList<Submission> {
    public SubmissionList(Submission[] submissions){
        super(List.of(submissions));
    }
    public SubmissionList(){
        super();
    }
}
