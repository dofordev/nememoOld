package com.soo.nememo.item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoGroup implements Serializable {
    private Long id;
    private Long seq;
    private int type;
    private String title;
    private String date;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public MemoGroup(){
    }

    public MemoGroup(Long id, Long seq, String title,int type, Date date){
        this.id = id;
        this.seq = seq;
        this.title = title;
        this.type= type;
        this.date = dateFormat.format(date);
    }

    public Long getId() {
        return id;
    }

    public Long getSeq() {
        return seq;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        try{
            Date date = dateFormat.parse(this.date);
            return date;
        }
        catch (Exception e){
            return new Date();
        }

    }

    public void setId(Long id){
        this.id = id;
    }
    public void setSeq(Long seq){
        this.seq = seq;
    }
    public void setType(int type){
        this.type = type;
    }
    public void setTitle(String title){
        this.title = title;
    }


    public void setDate(){

        this.date = dateFormat.format(date);;
    }

}
