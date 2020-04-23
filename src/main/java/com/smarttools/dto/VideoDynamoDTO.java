package com.smarttools.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "VIDEO")
public class VideoDynamoDTO {
    @DynamoDBHashKey(attributeName = "_id")
    public String _id;
    
	@DynamoDBAttribute(attributeName = "tittle")
    public String tittle;

    @DynamoDBAttribute(attributeName = "firstName")
    public String firstName;

    @DynamoDBAttribute(attributeName = "lastName")
    public String lastName;

    @DynamoDBAttribute(attributeName = "state")
    public String state;

    @DynamoDBAttribute(attributeName = "originalVideoPath")
    public String originalVideoPath;

    @DynamoDBAttribute(attributeName = "convertedVideoPath")
    public String convertedVideoPath;

     @DynamoDBAttribute(attributeName = "createDate")
    public String createDate;

    @DynamoDBAttribute(attributeName = "email")
    public String email;
    
    @DynamoDBAttribute(attributeName = "idContest")
    public String idContest;

    @DynamoDBAttribute(attributeName = "idUser")
    public String idUser;

    private String url;
    
    private String result;

    public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public String getTittle() {
		return tittle;
	}


	public void setTittle(String tittle) {
		this.tittle = tittle;
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


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}

	public String getConvertedVideoPath() {
		return convertedVideoPath;
	}


	public void setConvertedVideoPath(String convertedVideoPath) {
		this.convertedVideoPath = convertedVideoPath;
	}


	public String getCreateDate() {
		return createDate;
	}


	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getIdContest() {
		return idContest;
	}


	public void setIdContest(String idContest) {
		this.idContest = idContest;
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


	public String getOriginalVideoPath() {
		return originalVideoPath;
	}


	public void setOriginalVideoPath(String originalVideoPath) {
		this.originalVideoPath = originalVideoPath;
	}


	public String get_id() {
		return _id;
	}


	public void set_id(String _id) {
		this._id = _id;
	}

   
    @Override
    public String toString() {
        return "{" +
                "\"_id\": \'" + _id + "\'" +
                ", \"firstName\": \'" + firstName + "\'" +
                ", \"lastName\": \'" + lastName + "\'" +
                ", \"state\": \'" + state + "\'" +
                ", \"originalVideoPath\": \'" + originalVideoPath + "\'" +
                ", \"convertedVideoPath\": \'" + convertedVideoPath + "\'" +
                ", \"tittle\": \'" + tittle + "\'" +
                ", \"createDate\": " + createDate +
                ", \"email\": \'" + email + "\'" +
                ", \"idContest\": \'" + idContest + "\'" +
                ", \"idUser\": \'" + idUser + "\'" +
                ", \"url\": \'" + url + "\'" +
                "}";
    }
}
