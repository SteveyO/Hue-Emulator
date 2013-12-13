package com.hueemulator.emulator;

import java.util.Timer;

import org.json.JSONObject;

public class PHScheduleTimer extends Timer {
    
    private String scheduleIdentifier;
    private JSONObject commandJSON;

    public String getScheduleIdentifier() {
        return scheduleIdentifier;
    }

    public void setScheduleIdentifier(String scheduleIdentifier) {
        this.scheduleIdentifier = scheduleIdentifier;
    }

    public JSONObject getCommandJSON() {
        return commandJSON;
    }

    public void setCommandJSON(JSONObject commandJSON) {
        this.commandJSON = commandJSON;
    }

}
