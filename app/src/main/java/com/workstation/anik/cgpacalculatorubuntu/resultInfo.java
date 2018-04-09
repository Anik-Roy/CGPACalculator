package com.workstation.anik.cgpacalculatorubuntu;

/**
 * Created by anik on 11/24/17.
 */

public class resultInfo {
    private Integer id;
    private Double credit, gpa, totalGpa;

    public resultInfo() {
    }

    public resultInfo(Integer id, Double credit, Double gpa, Double totalGpa) {
        this.id = id;
        this.credit = credit;
        this.gpa = gpa;
        this.totalGpa = totalGpa;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTotalGpa() {
        return totalGpa;
    }

    public void setTotalGpa(Double totalGpa) {
        this.totalGpa = totalGpa;
    }

    public Double getGpa() {

        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public Double getCredit() {

        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }
}