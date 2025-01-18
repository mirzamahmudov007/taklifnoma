package com.taklifnoma.taklifnomalar.service;

import com.taklifnoma.taklifnomalar.entity.FileStorage;
import com.taklifnoma.taklifnomalar.entity.enums.FileStorageStatus;
import com.taklifnoma.taklifnomalar.repository.FileStorageRepository;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
public class FileStorageService {
    private final FileStorageRepository fileStorageRepository;

    //  hashId yasab olamiz
    private final Hashids hashids;

    public FileStorageService(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
        this.hashids = new Hashids(getClass().getName(), 6);
    }
    // application.yml ga yoziladi:    upload.server.folder: D:/NewFolder
    // build.gradle ga yoziladi:    implementation 'org.hashids:hashids:1.0.3'
    @Value("${upload.server.folder}")
    private String serverFolderPath;


    // save mathodi
    public FileStorage save(MultipartFile multipartFile){
        FileStorage fileStorage = new FileStorage();
        fileStorage.setName(multipartFile.getOriginalFilename());
        fileStorage.setFileSize(multipartFile.getSize());
        fileStorage.setContentType(multipartFile.getContentType());
        fileStorage.setFileStorageStatus(FileStorageStatus.DRAFT);
        fileStorage.setExtension(getExtension(multipartFile.getOriginalFilename()));  // pastda getExtension degan method bor
        fileStorage = fileStorageRepository.save(fileStorage);

        // file ni quyidagi formatda save qilamiz
        // serverFolderPath/upload_files/year/month/day/hashidNomi.extension      (extension: pdf, doc, jpg ...)
        // /serverFolderPath/upload_files/2022/04/24/dsfsdvsd.pdf

        //bu yerda serverFolderPath-static path; upload_folder-dynamic path

        // ESLATMAAAA  Date() funksiyasi 1900 yildan boshlanadi, shuning uchun 1900 yilni qoshib qoyamiz
        // ESLATMAAAA  Date() funksiyasida oy boshi 0 dan boshlanadi, shuning uchun 1 ni qoshib qoyamiz


        Date now = new Date();

        String path = String.format("%s/upload_files/%d/%d/%d/",
                this.serverFolderPath, 1900+now.getYear(), 1+now.getMonth(), now.getDate());
        File uploadFolder = new File(path);
        if (!uploadFolder.exists() && uploadFolder.mkdirs()){
            System.out.println("Folder created");
        }

        fileStorage.setHashId(hashids.encode(fileStorage.getId())); // fileStorage ning id sidan hashId yasayabdi

        String pathLocal = String.format("/upload_files/%d/%d/%d/%s.%s",
                1900+now.getYear(), 1+now.getMonth(), now.getDate(), fileStorage.getHashId(), fileStorage.getExtension());

        fileStorage.setUploadFolder(pathLocal);
        fileStorageRepository.save(fileStorage);
        uploadFolder = uploadFolder.getAbsoluteFile();

        File file = new File(uploadFolder, String.format("%s.%s", fileStorage.getHashId(), fileStorage.getExtension()));

        try{
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileStorage;
    }

    public FileStorage saves(String fileName, byte[] fileData, String contentType) {
        try {
            FileStorage fileStorage = new FileStorage();
            fileStorage.setName(fileName);
            fileStorage.setFileSize((long) fileData.length);
            fileStorage.setContentType(contentType);
            fileStorage.setFileStorageStatus(FileStorageStatus.DRAFT);
            fileStorage.setExtension(getExtension(fileName));
            fileStorage = fileStorageRepository.save(fileStorage);

            Date now = new Date();

            // Create directory structure
            String path = String.format("%s/upload_files/%d/%d/%d/",
                    this.serverFolderPath,
                    1900 + now.getYear(),
                    1 + now.getMonth(),
                    now.getDate());

            File uploadFolder = new File(path);
            if (!uploadFolder.exists() && uploadFolder.mkdirs()) {
                System.out.println("Folder created");
            }

            // Generate hashId
            fileStorage.setHashId(hashids.encode(fileStorage.getId()));

            // Create relative path
            String pathLocal = String.format("/upload_files/%d/%d/%d/%s.%s",
                    1900 + now.getYear(),
                    1 + now.getMonth(),
                    now.getDate(),
                    fileStorage.getHashId(),
                    fileStorage.getExtension());

            fileStorage.setUploadFolder(pathLocal);
            fileStorage = fileStorageRepository.save(fileStorage);

            // Save actual file
            File file = new File(uploadFolder.getAbsolutePath(),
                    String.format("%s.%s", fileStorage.getHashId(), fileStorage.getExtension()));

            Files.write(file.toPath(), fileData);

            return fileStorage;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }
    public String save(String fileName, byte[] fileData, String contentType) {
        try {
            FileStorage fileStorage = new FileStorage();
            fileStorage.setName(fileName);
            fileStorage.setFileSize((long) fileData.length);
            fileStorage.setContentType(contentType);
            fileStorage.setFileStorageStatus(FileStorageStatus.DRAFT);
            fileStorage.setExtension(getExtension(fileName));
            fileStorage = fileStorageRepository.save(fileStorage);

            Date now = new Date();

            // Create directory structure
            String path = String.format("%s/upload_files/%d/%d/%d/",
                    this.serverFolderPath,
                    1900 + now.getYear(),
                    1 + now.getMonth(),
                    now.getDate());

            File uploadFolder = new File(path);
            if (!uploadFolder.exists() && uploadFolder.mkdirs()) {
                System.out.println("Folder created");
            }

            // Generate hashId
            fileStorage.setHashId(hashids.encode(fileStorage.getId()));

            // Create relative path
            String pathLocal = String.format("/upload_files/%d/%d/%d/%s.%s",
                    1900 + now.getYear(),
                    1 + now.getMonth(),
                    now.getDate(),
                    fileStorage.getHashId(),
                    fileStorage.getExtension());

            fileStorage.setUploadFolder(pathLocal);
            fileStorageRepository.save(fileStorage);

            // Save actual file
            File file = new File(uploadFolder.getAbsolutePath(),
                    String.format("%s.%s", fileStorage.getHashId(), fileStorage.getExtension()));

            Files.write(file.toPath(), fileData);

            return pathLocal;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    // file preview and download
    public FileStorage findByHashId(String hashId){
        return fileStorageRepository.findByHashId(hashId);
    }

    public void delete(String hashId){
        FileStorage fileStorage = fileStorageRepository.findByHashId(hashId);
        File file = new File(String.format("%s/%s", this.serverFolderPath, fileStorage.getUploadFolder()));
        if (file.delete()){
            fileStorageRepository.delete(fileStorage);
        }
    }

    private String getExtension(String fileName){
        String extension = null;
        if (fileName != null && !fileName.isEmpty()){
            int dot = fileName.lastIndexOf('.');
            if (dot > 0 && dot <= fileName.length()-2){
                extension = fileName.substring(dot+1);
            }
        }
        return extension;
    }
}

