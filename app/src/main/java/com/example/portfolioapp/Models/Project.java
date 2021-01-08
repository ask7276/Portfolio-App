package com.example.portfolioapp.Models;

public class Project {
    private String Project_Title, Email, Desc;

    public Project(String project_Title, String email, String desc) {
        Project_Title = project_Title;
        Email = email;
        Desc = desc;
    }

    public String getDesc() {
        return Desc;
    }

    public String getProject_Title() {
        return Project_Title;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
