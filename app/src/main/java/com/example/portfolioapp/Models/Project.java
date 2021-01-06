package com.example.portfolioapp.Models;

public class Project {
    private String Project_Title, Email;

    public Project(){

    }

    public Project(String project_Title, String email) {
        Project_Title = project_Title;
        Email = email;
    }

    public String getProject_Title() {
        return Project_Title;
    }

    public void setProject_Title(String project_Title) {
        Project_Title = project_Title;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
