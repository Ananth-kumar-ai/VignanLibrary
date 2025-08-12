package org.vignanuniversity.vignanlibrary.Adapter;


public class URL {
    private static final String ORG_BASE_URL = "http://192.168.10.18/";
    private static final String PUBLIC_BASE_URL = "http://14.139.85.171/";
    private static String baseURL = "http://160.187.169.12/";

    static final String apiURL = "jspapi/";

    static final String spFolder = "sp/";
    static final String registrationNo = "?regno=";
    static final String year_ = "&year=";
    static final String sem_ = "&sem=";
    static final String fileFinalMarks = "finalmarks_api.jsp?regno=";
    static final String imageExtension = ".jpg";
    static final String filestuFee = "stu_fee_new1.jsp";
    static final String fileInternalMarks = "internal_main.jsp";
    static final String filePersonalDetails = "personal_details.jsp";
    static final String fileMainAttendance = "main_attendance.jsp";
    static final String fileAggregateApi = "aggregate_api.jsp";
    static final String fileCounsellorInfoApi = "counsellor_info_api.jsp";
    static final String fileStuCredits = "stuCredits.jsp";
    static final String fileChangePass = "change_pass.jsp";
    static final String fileForgotPass = "forgot_pass_api.jsp";

    static final String fileOtp_verifi = "otp_verifi.jsp";
    static final String fileLogin = "loginapi.jsp";

    public URL() {
    }

    public static String getPersonalDetailsUrl(String regno) {
        return baseURL + apiURL + filePersonalDetails + registrationNo + regno;
    }

    public static String getMainAttendanceUrl(String regno) {
        return baseURL + apiURL + fileMainAttendance + registrationNo + regno;
    }

    public static String getAggregateApiUrl(String regno) {
        return baseURL + apiURL + fileAggregateApi + registrationNo + regno;
    }

    public static String getCounsellorInfoApiUrl(String regno) {
        return baseURL + apiURL + fileCounsellorInfoApi + registrationNo + regno;
    }

    public static String getStuCreditsUrl(String regno) {
        return baseURL + apiURL + fileStuCredits + registrationNo + regno;
    }

    public static String getChangePassUrl() {
        return baseURL + apiURL + fileChangePass;
    }

    public static String getLoginUrl() {
        return baseURL + apiURL + fileLogin;
    }

    public static String getFinalMarksUrl(String regno, String year, String sem) {
        return baseURL + apiURL + fileFinalMarks + regno + year_ + year + sem_ + sem;
    }

    public static String getStuFeeUrl(String regno) {
        return baseURL + apiURL + filestuFee + registrationNo + regno;
    }

    public static String getInternalMarksUrl(String regno) {
        return baseURL + apiURL + fileInternalMarks + registrationNo + regno;
    }

    public static String getForgotPassUrl() {
        return baseURL + apiURL + fileForgotPass;
    }

    public static String getOtp_verifiUrl() {
        return baseURL + apiURL + fileOtp_verifi;
    }

    public static String getStudentImageWithReg(String regno) {
        return "http://160.187.169.11/aeps/StudentPhotos/" + regno + imageExtension;
    }
    public static String getStudentImageWithReg1(String regno) {
        return "http://160.187.169.14/jspapi/photos/" + regno + ".JPG";
    }
}
