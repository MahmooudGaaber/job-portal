package com.example.jobportal.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class FileUploadUtil {

    public static void saveFile(String uploadDir
            ,String fileName
            ,MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        try(InputStream inputStream = multipartFile.getInputStream()) {
            Path path = uploadPath.resolve(fileName);
            System.out.println("File Path"+path);
            System.out.println("File Name"+fileName);
            Files.copy(inputStream,path , StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception ioe){
            throw  new IOException("File Not Saved"+fileName);
        }

    }

}
