package com.getjobtzben.projecttitle.model;

public class Project {
    String image;
    String title;
    String projDes;
    String sourceCodePrice;
    String custCodePrice;
    String projectId;

    public Project() {
    }

    public Project(String image, String title, String projDes, String sourceCodePrice, String custCodePrice, String projectId) {
        this.image = image;
        this.title = title;
        this.projDes = projDes;
        this.sourceCodePrice = sourceCodePrice;
        this.custCodePrice = custCodePrice;
        this.projectId = projectId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProjDes() {
        return projDes;
    }

    public void setProjDes(String projDes) {
        this.projDes = projDes;
    }

    public String getSourceCodePrice() {
        return sourceCodePrice;
    }

    public void setSourceCodePrice(String sourceCodePrice) {
        this.sourceCodePrice = sourceCodePrice;
    }

    public String getCustCodePrice() {
        return custCodePrice;
    }

    public void setCustCodePrice(String custCodePrice) {
        this.custCodePrice = custCodePrice;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
