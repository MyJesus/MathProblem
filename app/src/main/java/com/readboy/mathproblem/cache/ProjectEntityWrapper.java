package com.readboy.mathproblem.cache;

import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.http.response.ProjectEntity;

import java.util.List;

/**
 * Created by oubin on 2017/9/22.
 */

public class ProjectEntityWrapper {

    private SubjectType type;
    private int grade;
    private List<ProjectEntity.Project> projectList;

    public ProjectEntityWrapper() {
    }

    public ProjectEntityWrapper(SubjectType type, int grade) {
        this.type = type;
        this.grade = grade;
    }

    public ProjectEntityWrapper(SubjectType type, int grade, List<ProjectEntity.Project> projectList) {
        this.type = type;
        this.grade = grade;
        this.projectList = projectList;
    }

    public SubjectType getType() {
        return type;
    }

    public void setType(SubjectType type) {
        this.type = type;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public List<ProjectEntity.Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectEntity.Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public String toString() {
        return "ProjectEntityWrapper{" +
                "type=" + type +
                ", grade=" + grade +
                '}';
    }
}
