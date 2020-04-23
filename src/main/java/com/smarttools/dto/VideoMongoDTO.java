package com.smarttools.dto;


public class VideoMongoDTO {
	
	public String _id;
	
	public String id;
    
	public String title;

    public String first_name;

    public String last_name;

    public String status;

    public String converted_video_path;

    public String original_video_path;

    public String date;

    public String email;
    
    public String contest_id;

    public String idUser;

    private String url;
    
    private String result;
    
    public String video_upload;
    

    public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public String get_id() {
		return _id;
	}


	public void set_id(String _id) {
		this._id = _id;
	}


	public String getFirst_name() {
		return first_name;
	}


	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}


	public String getLast_name() {
		return last_name;
	}


	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getConverted_video_path() {
		return converted_video_path;
	}


	public void setConverted_video_path(String converted_video_path) {
		this.converted_video_path = converted_video_path;
	}


	public String getOriginal_video_path() {
		return original_video_path;
	}


	public void setOriginal_video_path(String original_video_path) {
		this.original_video_path = original_video_path;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdUser() {
		return idUser;
	}


	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	

	   
    @Override
    public String toString() {
        return "{" +
                "\"_id\": \'" + _id + "\'" +
                ", \"first_name\": \'" + first_name + "\'" +
                ", \"last_name\": \'" + last_name + "\'" +
                ", \"status\": \'" + status + "\'" +
                ", \"original_video_path\": \'" + original_video_path + "\'" +
                ", \"converted_video_path\": \'" + converted_video_path + "\'" +
                ", \"title\": \'" + title + "\'" +
                ", \"date\": " + date +
                ", \"email\": \'" + email + "\'" +
                ", \"contest_id\": \'" + contest_id + "\'" +
                ", \"idUser\": \'" + idUser + "\'" +
                ", \"url\": \'" + url + "\'" +
                "}";
    }


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getContest_id() {
		return contest_id;
	}


	public void setContest_id(String contest_id) {
		this.contest_id = contest_id;
	}


	public String getVideo_upload() {
		return video_upload;
	}


	public void setVideo_upload(String video_upload) {
		this.video_upload = video_upload;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
}
