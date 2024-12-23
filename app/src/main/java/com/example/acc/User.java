package com.example.acc;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    public User() {
        // Default constructor
    }

    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    // Save user data to SharedPreferences
    public void saveToLocalStorage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }   

    // Load user data from SharedPreferences
    public static User loadFromLocalStorage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        User user = new User();
        user.setUserId(prefs.getString(KEY_USER_ID, ""));
        user.setName(prefs.getString(KEY_NAME, ""));
        user.setEmail(prefs.getString(KEY_EMAIL, ""));
        user.setPhone(prefs.getString(KEY_PHONE, ""));
        return user;
    }

    // Clear user data from SharedPreferences
    public static void clearLocalStorage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
