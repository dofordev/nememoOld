package com.soo.nememo.item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoItem implements Serializable {
    private Long id;
    private Long groupId;
    private Long seq;
    private String contents;



    private int checked;
    private String date;



    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public TodoItem(){
    }

    public TodoItem(Long id, Long groupId,  String contents, int checked, Date date){
        this.id = id;
        this.contents = contents;

        this.checked = checked;
        this.date = dateFormat.format(date);
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public Long getId() {
        return id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getSeq() {
        return seq;
    }

    public String getContents() {
        return contents;
    }


    public String getDate() {
        return this.date;
        /*try{
            Date date = dateFormat.parse(this.date);
            return date;
        }
        catch (Exception e){
            return new Date();
        }
        */

    }

    public void setId(Long id){
        this.id = id;
    }
    public void setGroupId(Long groupId){
        this.groupId = groupId;
    }
    public void setSeq(Long seq){
        this.seq = seq;
    }
    public void setContents(String contents){
        this.contents = contents;
    }

    public void setDate(String date){
        this.date = date;
    }
    public void setDate(Date date){
        this.date = dateFormat.format(date);
    }

}
