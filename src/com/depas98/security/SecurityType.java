package com.depas98.security;

public enum SecurityType {
    DOOR ("door", "Door"),
    ALARM ("alarm", "Alarm"),
    IMG("img", "Image") ,
    UNKNOWN("Unknown", "Unknown");

    final private String name;
    final private String id;

    SecurityType(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public static SecurityType getSecurityTypeByID(final String id){
        if (id==null){
            throw new IllegalArgumentException("ID can't be null");
        }

        if (DOOR.getId().equalsIgnoreCase(id)){
            return DOOR;
        }
        else if (ALARM.getId().equalsIgnoreCase(id)){
            return ALARM;
        }
        else if (IMG.getId().equalsIgnoreCase(id)){
            return IMG;
        }
        else{
            return UNKNOWN;
        }

    }
}
