package cn.edu.pku.wuliangliang.bean;

/**
 * Created by WLL on 2017/11/30.
 */

public class Date {
    private int status;
    private int year;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private int month;
    private int day;
    private int lunarYear;
    private int lunarMonth;
    private int lunarDay;
    private String cnYear;
    private String cnMonth;
    private String cnDay;
    private String animal;
    private String week;
    private boolean leap;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getLunarYear() {
        return lunarYear;
    }

    public void setLunarYear(int lunarYear) {
        this.lunarYear = lunarYear;
    }

    public int getLunarMonth() {
        return lunarMonth;
    }

    public void setLunarMonth(int lunarMonth) {
        this.lunarMonth = lunarMonth;
    }

    public int getLunarDay() {
        return lunarDay;
    }

    public void setLunarDay(int lunarDay) {
        this.lunarDay = lunarDay;
    }

    public String getCnYear() {
        return cnYear;
    }

    public void setCnYear(String cnYear) {
        this.cnYear = cnYear;
    }

    public String getCnMonth() {
        return cnMonth;
    }

    public void setCnMonth(String cnMonth) {
        this.cnMonth = cnMonth;
    }

    public String getCnDay() {
        return cnDay;
    }

    public void setCnDay(String cnDay) {
        this.cnDay = cnDay;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public boolean isLeap() {
        return leap;
    }

    public void setLeap(boolean leap) {
        this.leap = leap;
    }
}
