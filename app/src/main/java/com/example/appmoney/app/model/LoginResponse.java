package com.example.appmoney.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LoginResponse {

    // Khớp với JSON response bạn đã paste
    @SerializedName("Success")
    private boolean success;

    @SerializedName("Message")
    private String message;

    @SerializedName("UserTokenKey")
    private String userTokenKey;

    @SerializedName("LoggedIn")
    private boolean loggedIn;

    @SerializedName("LoginName")
    private String loginName;

    @SerializedName("CurrentUser")
    private CurrentUser currentUser;

    @SerializedName("Data")
    private Data data;

    // Getters
    public boolean isSuccess()          { return success; }
    public String getMessage()          { return message; }
    public String getUserTokenKey()     { return userTokenKey; }
    public boolean isLoggedIn()         { return loggedIn; }
    public String getLoginName()        { return loginName; }
    public CurrentUser getCurrentUser() { return currentUser; }
    public Data getData()               { return data; }

    // ══ CurrentUser ══════════════════════════════════════
    public static class CurrentUser {

        @SerializedName("ID")
        private int id;

        @SerializedName("Name")
        private String name;

        @SerializedName("Email")
        private String email;

        @SerializedName("LoginName")
        private String loginName;

        @SerializedName("Mobile")
        private String mobile;

        @SerializedName("AvatarUrl")
        private String avatarUrl;

        @SerializedName("IsSiteAdmin")
        private boolean isSiteAdmin;

        @SerializedName("IsLock")
        private boolean isLock;

        @SerializedName("IsAnonymous")
        private boolean isAnonymous;

        @SerializedName("WebID")
        private String webId;

        @SerializedName("SiteID")
        private String siteId;

        public int    getId()        { return id; }
        public String getName()      { return name; }
        public String getEmail()     { return email; }
        public String getLoginName() { return loginName; }
        public String getMobile()    { return mobile; }
        public String getAvatarUrl() { return avatarUrl; }
        public boolean isSiteAdmin() { return isSiteAdmin; }
        public boolean isLock()      { return isLock; }
        public String getWebId()     { return webId; }
        public String getSiteId()    { return siteId; }
    }

    // ══ Data (chứa Roles và Permissions) ═════════════════
    public static class Data {

        @SerializedName("Roles")
        private List<Role> roles;

        @SerializedName("Permissions")
        private String permissions;

        @SerializedName("TwoFactorAuthentication")
        private boolean twoFactorAuthentication;

        public List<Role> getRoles()   { return roles; }
        public String getPermissions() { return permissions; }
        public boolean isTwoFactor()   { return twoFactorAuthentication; }
    }

    // ══ Role ══════════════════════════════════════════════
    public static class Role {

        @SerializedName("ID")
        private int id;

        @SerializedName("Name")
        private String name;

        @SerializedName("Description")
        private String description;

        @SerializedName("Type")
        private String type;

        public int    getId()          { return id; }
        public String getName()        { return name; }
        public String getDescription() { return description; }
        public String getType()        { return type; }
    }
}