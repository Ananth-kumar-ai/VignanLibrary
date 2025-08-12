package org.vignanuniversity.vignanlibrary;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class StudentData implements Parcelable {

    private String regno,name,gender, branch, branchcode, course, coursecode, section, studentmobile, studentemailid, parentemailid, fathermobile;
    private int semester;
    public StudentData() {
    }

    public StudentData(String regno, String name, String gender, String branch, String branchcode, String course, String coursecode, String section, String studentmobile, String studentemailid, String parentemailid, String fathermobile, int semester) {
        this.regno = regno;
        this.name = name;
        this.gender = gender;
        this.branch = branch;
        this.branchcode = branchcode;
        this.course = course;
        this.coursecode = coursecode;
        this.section = section;
        this.studentmobile = studentmobile;
        this.studentemailid = studentemailid;
        this.parentemailid = parentemailid;
        this.fathermobile = fathermobile;
        this.semester = semester;
    }

    protected StudentData(Parcel in) {
        regno = in.readString();
        name = in.readString();
        gender = in.readString();
        branch = in.readString();
        branchcode = in.readString();
        course = in.readString();
        coursecode = in.readString();
        section = in.readString();
        studentmobile = in.readString();
        studentemailid = in.readString();
        parentemailid = in.readString();
        fathermobile = in.readString();
        semester = in.readInt();
    }

    public static final Creator<StudentData> CREATOR = new Creator<StudentData>() {
        @Override
        public StudentData createFromParcel(Parcel in) {
            return new StudentData(in);
        }

        @Override
        public StudentData[] newArray(int size) {
            return new StudentData[size];
        }
    };

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBranchcode() {
        return branchcode;
    }

    public void setBranchcode(String branchcode) {
        this.branchcode = branchcode;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(String coursecode) {
        this.coursecode = coursecode;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStudentmobile() {
        return studentmobile;
    }

    public void setStudentmobile(String studentmobile) {
        this.studentmobile = studentmobile;
    }

    public String getStudentemailid() {
        return studentemailid;
    }

    public void setStudentemailid(String studentemailid) {
        this.studentemailid = studentemailid;
    }

    public String getParentemailid() {
        return parentemailid;
    }

    public void setParentemailid(String parentemailid) {
        this.parentemailid = parentemailid;
    }

    public String getFathermobile() {
        return fathermobile;
    }

    public void setFathermobile(String fathermobile) {
        this.fathermobile = fathermobile;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentData)) return false;
        StudentData that = (StudentData) o;
        return getSemester() == that.getSemester() && Objects.equals(getRegno(), that.getRegno()) && Objects.equals(getName(), that.getName()) && Objects.equals(getGender(), that.getGender()) && Objects.equals(getBranch(), that.getBranch()) && Objects.equals(getBranchcode(), that.getBranchcode()) && Objects.equals(getCourse(), that.getCourse()) && Objects.equals(getCoursecode(), that.getCoursecode()) && Objects.equals(getSection(), that.getSection()) && Objects.equals(getStudentmobile(), that.getStudentmobile()) && Objects.equals(getStudentemailid(), that.getStudentemailid()) && Objects.equals(getParentemailid(), that.getParentemailid()) && Objects.equals(getFathermobile(), that.getFathermobile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegno(), getName(), getGender(), getBranch(), getBranchcode(), getCourse(), getCoursecode(), getSection(), getStudentmobile(), getStudentemailid(), getParentemailid(), getFathermobile(), getSemester());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(regno);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(branch);
        dest.writeString(branchcode);
        dest.writeString(course);
        dest.writeString(coursecode);
        dest.writeString(section);
        dest.writeString(studentmobile);
        dest.writeString(studentemailid);
        dest.writeString(parentemailid);
        dest.writeString(fathermobile);
        dest.writeInt(semester);
    }
}
