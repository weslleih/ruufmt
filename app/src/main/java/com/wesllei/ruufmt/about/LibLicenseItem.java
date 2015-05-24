package com.wesllei.ruufmt.about;

/**
 * Created by wesllei on 11/05/15.
 */
public class LibLicenseItem {
    private String libTitle;
    private String licenseTitle;
    private String licenseBody;

    public LibLicenseItem(String libTitle, String licenseTitle, String licenseBody) {
        this.libTitle = libTitle;
        this.licenseTitle = licenseTitle;
        this.licenseBody = licenseBody;
    }

    public String getLibTitle() {
        return libTitle;
    }

    public String getLicenseTitle() {
        return licenseTitle;
    }

    public String getLicenseBody() {
        return licenseBody;
    }
}
