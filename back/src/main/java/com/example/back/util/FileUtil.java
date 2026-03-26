package com.example.back.util;


import com.example.back.entity.ProductImageEntity;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * FileName    : FileUtil
 * Since       : 26. 3. 26.
 * Dsecription  : 파일명 추출, uuid 생성 등의 기능구현
 */

@Slf4j
@Component
public class FileUtil {

    @Value("${FILE_PATH}")
    private String fileDir;

    /*
     * 서버 시작 시 저장 폴더가 없으면 자동 생성
     * */
    @PostConstruct
    public void init() {

        // file:/// 접두사가 붙어있으므로 제거하여 경로 추출
        String path = fileDir.replace("file:///", "");

        File folder = new File(path);

        // 폴더가 없으면 생성
        if (!folder.exists()) {
            folder.mkdir();
            log.info("파일 저장 폴더 생성 완료: {}", path);
        }
    }


    /*
     * 파일 전체 경로 반환
     * */
    public String getFullPath(String filename) {
        String path = fileDir.replace("file:///", "");
        return path + filename;
    }

    /*
     * 여러 개의 상품 이미지 저장 및 엔티티 리스트 반환
     * */
    public List<ProductImageEntity> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        List<ProductImageEntity> storeFileResult = new ArrayList<>();

        if (multipartFiles != null) {
            for (int i = 0; i < multipartFiles.size(); i++) {
                MultipartFile file = multipartFiles.get(i);

                if (!file.isEmpty()) {
                    // 첫 번째 이미지(index 0)를 대표 이미지(repImgYn = true)로 설정
                    boolean isFirst = (i == 0);
                    storeFileResult.add(storeFile(file, isFirst));
                }
            }
        }
        return storeFileResult;
    }


    /*
    *  단일 상품 이미지 저장 및 엔티티 생성
    * */
    public ProductImageEntity storeFile(MultipartFile multipartFile, boolean isReqImg) throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String savedFileName = createStoreFileName(originalFilename);

        // 파일 저장
        multipartFile.transferTo(new File(getFullPath(savedFileName)));
        log.info("[상품 이미지 저장] 원본명: {}, 저장명: {}", originalFilename, savedFileName);

        // DB 저장용 엔티티 객체 생성 (빌더 패턴)
        return ProductImageEntity.builder()
                .originalFileName(originalFilename)
                .savedFileName(savedFileName)
                .repImgYn(isReqImg)
                .build();
    }


    /*
     * UUID 파일명 생성
     * */
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        return UUID.randomUUID().toString() + "." + ext;
    }

    /*
     * 확장자 추출
     * */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        // 파일명에 마침표가 없거나(-1), 마침표가 맨 마지막에 있는 경우에 대한 처리
        if (pos == -1 || pos == originalFilename.length() - 1) return "";
        return originalFilename.substring(pos + 1);
    }
}
