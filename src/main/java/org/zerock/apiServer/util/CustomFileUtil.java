package org.zerock.apiServer.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {


    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    // 생성자 대신에 많이 씀, 뭔가 초기화 할 때
    @PostConstruct
    public void init() {

        File tempFolder = new File(uploadPath);
        // 폴더가 없다면 폴더를 만들어준다.
        if(!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        uploadPath = tempFolder.getAbsolutePath();
        log.info("-------------------------------");
        log.info(uploadPath);
    }
    // 파일 여러개 업로드 작업
    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
        if(files == null || files.size() == 0) {
            return null;
        }

        // 업로드 된 파일들 이름
        List<String> uploadNames = new ArrayList<>();
        for(MultipartFile file : files) {
            // 파일 업로드 되면 32자리 UUID가 만들어짐
            String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path savePath = Paths.get(uploadPath, savedName);
            try {
                // 원본 파일 업로드 코드
                Files.copy(file.getInputStream(), savePath);
                // 이미지인 경우에만 섬네일을 만들어주자.
                String contentType = file.getContentType(); // Mime type 이 나옴
                if(contentType != null || contentType.startsWith("image")) {
                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
                    // 긴 쪽을 200으로 고정
                    Thumbnails.of(savePath.toFile()).size(200,200).toFile(thumbnailPath.toFile());
                }
                uploadNames.add(savedName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return uploadNames;
    }

    // 파일 조회 기능
    public ResponseEntity<Resource> getFile(String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        if(!resource.isReadable()) {
            resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
        }
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // 파일 삭제 기능
    public void deleteFiles(List<String> fileNames) {
        if(fileNames == null || fileNames.isEmpty())
            return ;
        fileNames.forEach(fileName -> {
            // 썸네일이 있으면 썸네일도 삭제
            String thumbnailFileName = "s_" + fileName;
            Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
            Path filePath = Paths.get(uploadPath,fileName);

            try {
                Files.deleteIfExists(thumbnailPath);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
