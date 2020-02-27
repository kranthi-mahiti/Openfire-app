package com.sanjay.openfire.chat.models.UserModel;

public class UserModel {
    private String userName;
    private String firstName;
    private String lastName;
    private String emailHome;
    private String middleName;
    private String nickName;
    private String phoneHome;
    private String organization;
    private String jabberId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailHome() {
        return emailHome;
    }

    public void setEmailHome(String emailHome) {
        this.emailHome = emailHome;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getJabberId() {
        return jabberId;
    }

    public void setJabberId(String jabberId) {
        this.jabberId = jabberId;
    }
    //
//      vcard.setFirstName(userName);
//        vcard.setLastName("Sci");
//        vcard.setEmailHome(userName + "@gmail.com");
//        vcard.setMiddleName("Developer");
//        vcard.setNickName("SCIBD");
//        vcard.setPhoneHome("Voice", "12783849404");
//        vcard.setOrganization("Save the Children");
}
