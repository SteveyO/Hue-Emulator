package com.hueemulator.emulator;

import java.util.HashMap;


public class PHScheduleTimerManager {

    private static PHScheduleTimerManager scheduleManager;
    /**
     * Stores list of ScheduleIdentifiers and their Respective Timer Objects
     */
    private HashMap<String ,PHScheduleTimer> scheduleStore=new HashMap<String ,PHScheduleTimer>();
    
    
    /**
     * default private constructor for ScheduleTimer manager
     */
    private PHScheduleTimerManager() {
        
    }

    public static synchronized PHScheduleTimerManager getInstance(){
        if(scheduleManager==null){
            scheduleManager=new PHScheduleTimerManager();
        }
        return scheduleManager;
    }

    public void storeSchedule(String scheduleIdentifier, PHScheduleTimer schTimer){
        scheduleStore.put(scheduleIdentifier, schTimer);  
    }
    
    public void removeSchedule(String scheduleIdentifier){
        scheduleStore.remove(scheduleIdentifier);
    }
    
}
