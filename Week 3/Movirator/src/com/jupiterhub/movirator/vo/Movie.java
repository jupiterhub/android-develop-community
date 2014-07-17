package com.jupiterhub.movirator.vo;

public class Movie {
	private String title;
	private String year;
	private String runtime;
	private String imdbRating;
	
	public Movie(String title, String year, String runtime, String imdbRating) {
		super();
		this.title = title;
		this.year = year;
		this.runtime = runtime;
		this.imdbRating = imdbRating;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getRuntime() {
		return runtime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	public String getImdbRating() {
		return imdbRating;
	}
	public void setImdbRating(String imdbRating) {
		this.imdbRating = imdbRating;
	}
}