package com.android.taptap;

public class Messages {

    public Messages(){
    }

    public Messages(String message, Boolean seen, Long time, String type, String from) {
    this.message = message;
    this.seen = seen;
    this.time = time;
    this.type = type;
    this.from = from;
}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String message;
    public Boolean seen;
    public Long time;
    public String type;
    public String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
