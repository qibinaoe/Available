package com.scuavailable.available.school;

public class RequestModifyBean {


    /**
     * id : 0
     * teacherID : 1234
     * studentID : 1234
     * courseID : 1234
     * sectionID : 1234
     * testType : 1234
     * testTime : 1234
     * testRoom : 1234
     * testBuilding : 1234
     * requestTime : 1234
     * reason : 1234
     * requestType : 0
     * isValid : 0
     */

    private int id;
    private String teacherID;
    private String studentID;
    private String courseID;
    private String sectionID;
    private String testType;
    private String testTime;
    private String testRoom;
    private String testBuilding;
    private String requestTime;
    private String reason;
    private int requestType;
    private int isValid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getTestRoom() {
        return testRoom;
    }

    public void setTestRoom(String testRoom) {
        this.testRoom = testRoom;
    }

    public String getTestBuilding() {
        return testBuilding;
    }

    public void setTestBuilding(String testBuilding) {
        this.testBuilding = testBuilding;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }
}
