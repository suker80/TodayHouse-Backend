package com.todayhouse.infra.S3Storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    List<String> uploadImages(List<MultipartFile> multipartFile);

    String uploadImage(MultipartFile multipartFile);

    byte[] getImage(String fileName);

    void delete(List<String> fileName);

    void deleteOne(String fileName);

    String changeFileNameToUrl(String fileName);

    String changeUrlToFileName(String url);
}
