package com.example.placementapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile")
public class UserProfile {
    @PrimaryKey
    @NonNull
    private String uid;

    private String fullname;
    private String address;
    private String email;
    private String dob;
    private String semester;
    private String profileImageUrl;
    private String phoneNumber;
    private String uucmsNo;
    private String resumeUrl;
    private String tenthMarkSheetUrl;
    private String TwelfthMarkSheetUrl;



    public UserProfile() {
    }


    public UserProfile(@NonNull String uid, String fullname, String address, String email, String dob, String semester,String phoneNumber,String uucmsNo) {
        this.uid = uid;
        this.fullname = fullname;
        this.address = address;
        this.email = email;
        this.dob = dob;
        this.phoneNumber=phoneNumber;
        this.uucmsNo=uucmsNo;
        this.semester = semester;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getTwelfthMarkSheetUrl() {
        return TwelfthMarkSheetUrl;
    }

    public void setTwelfthMarkSheetUrl(String twelfthMarkSheetUrl) {
        TwelfthMarkSheetUrl = twelfthMarkSheetUrl;
    }

    public String getTenthMarkSheetUrl() {
        return tenthMarkSheetUrl;
    }

    public void setTenthMarkSheetUrl(String tenthMarkSheetUrl) {
        this.tenthMarkSheetUrl = tenthMarkSheetUrl;
    }

    public String getUucmsNo() {
        return uucmsNo;
    }

    public void setUucmsNo(String uucmsNo) {
        this.uucmsNo = uucmsNo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
