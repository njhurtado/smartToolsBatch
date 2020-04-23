package com.smarttools.dto;

import java.util.Date;

public class VideoDTO {
	
	private Long idVideo;
    private String videoPath;
    private String convertedVideoPath;
    private String firstName;
    private String lasttName;
    private String state;
    private Date createDate;
    private String email;
    private String result;


	public VideoDTO() {
		// TODO Auto-generated constructor stub
	}


	public Long getIdVideo() {
		return idVideo;
	}


	public void setIdVideo(Long idVideo) {
		this.idVideo = idVideo;
	}


	public String getVideoPath() {
		return videoPath;
	}


	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}


	public String getConvertedVideoPath() {
		return convertedVideoPath;
	}


	public void setConvertedVideoPath(String convertedVideoPath) {
		this.convertedVideoPath = convertedVideoPath;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLasttName() {
		return lasttName;
	}


	public void setLasttName(String lasttName) {
		this.lasttName = lasttName;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}

}
