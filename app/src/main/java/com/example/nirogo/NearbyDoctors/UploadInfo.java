package com.example.nirogo.NearbyDoctors;

public class UploadInfo {


    String url, name, city, speciality, DocId,PhoneDoc;

    public UploadInfo() {
    }

    public UploadInfo(String url, String name, String speciality, String city,String DocId,String PhoneDoc) {
        this.url = url;
        this.name = name;
        this.city = city;
        this.speciality = speciality;
        this.DocId=DocId;
        this.PhoneDoc=PhoneDoc;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;}


    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDocId() {
        return DocId;
    }

    public void setDocId(String docId) {
        DocId = docId;
    }

    public String getPhoneDoc() {
        return PhoneDoc;
    }

    public void setPhoneDoc(String phoneDoc) {
        PhoneDoc = phoneDoc;
    }
}
