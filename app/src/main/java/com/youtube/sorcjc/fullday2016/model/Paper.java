package com.youtube.sorcjc.fullday2016.model;

public class Paper {
/*
{
  "id": 1,
  "speaker_id": 1,
  "subject": "Gestión de proyectos en la nube",
  "description": "",
  "realization": "2016-11-26",
  "start": "00:00:00",
  "end": "00:00:00",
  "enable": 1,
  "created_at": "2016-11-09 21:44:13",
  "updated_at": "2016-11-09 21:44:13"
}
*/
    private int id;
    private int speaker_id;
    private String subject;
    private String description;
    private String start;
    private String end;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpeaker_id() {
        return speaker_id;
    }

    public void setSpeaker_id(int speaker_id) {
        this.speaker_id = speaker_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
