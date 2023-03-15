package com.cst438.domain;

import java.sql.Date;

public class AssignmentDTO {
    public int id;
    public Course course;
    public String name;
    public Date dueDate;
    public int needsGrading;

    @Override
    public String toString() {
        return "Assignment [id=" + id + ", course_id=" + course.getCourse_id() + ", name=" + name + ", dueDate="
                + dueDate
                + ", needsGrading=" + needsGrading + "]";
    }
}
