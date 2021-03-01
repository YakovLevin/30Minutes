package com.jacob.a30minutesschedule;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Task {

    private String time, task;
    private boolean checked;

    public Task() {
        this.time = "12:00";
        this.task = "" ;
        this.checked = false;
    }

    public String getTime() {
        return time;
    }

    public String getTask() {
        return task;
    }




    public void setTime(String time) {
        this.time = time;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}

