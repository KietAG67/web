package com.university.model;
import java.io.Serializable;
public class MajorCount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String major;
    private int count;
    public MajorCount() { }
    public MajorCount(String major, int count) { this.major = major; this.count = count; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
