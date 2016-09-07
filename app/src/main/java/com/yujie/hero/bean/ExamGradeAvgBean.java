package com.yujie.hero.bean;

public class ExamGradeAvgBean {
	private float avgGrade;
	private String class_name;
	public float getAvgGrade() {
		return avgGrade;
	}
	public void setAvgGrade(float avgGrade) {
		this.avgGrade = avgGrade;
	}
	public String getClass_name() {
		return class_name;
	}
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	@Override
	public String toString() {
		return "ExamGradeAvgBean [avgGrade=" + avgGrade + ", class_name="
				+ class_name + "]";
	}
	public ExamGradeAvgBean(float avgGrade, String class_name) {
		super();
		this.avgGrade = avgGrade;
		this.class_name = class_name;
	}
	public ExamGradeAvgBean() {
		super();
	}
}
