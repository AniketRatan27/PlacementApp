package com.example.placementapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile userProfile);

    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    UserProfile getUserProfile(String uid);

    @Query("UPDATE user_profile SET resumeUrl = :resumeUrl Where uid = :uid")
    void updateResumeUrl(String uid, String resumeUrl);

    @Query("UPDATE user_profile SET tenthMarkSheetUrl = :tenthMarksheetUrl WHERE uid = :uid")
    void updateTenthMarksheetUrl(String uid, String tenthMarksheetUrl);

    @Query("UPDATE user_profile SET TwelfthMarkSheetUrl = :twelfthMarksheetUrl WHERE uid = :uid")
    void updateTwelfthMarksheetUrl(String uid, String twelfthMarksheetUrl);

    @Query("UPDATE user_profile SET phoneNumber = :phoneNo WHERE uid = :uid")
    void updatePhoneNo(String uid, String phoneNo);

    @Query("UPDATE user_profile SET uucmsNo = :uucmsno WHERE uid = :uid")
    void updateGender(String uid, String uucmsno);
}
