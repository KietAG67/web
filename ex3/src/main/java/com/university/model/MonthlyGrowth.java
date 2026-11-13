package com.university.model;
import java.io.Serializable;
public class MonthlyGrowth implements Serializable {
    private static final long serialVersionUID = 1L;
    private String month;
    private int count;
    public MonthlyGrowth() { }
    public MonthlyGrowth(String month, int count) { this.month = month; this.count = count; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
