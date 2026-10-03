//package com.ticket.ticketmanagement.service;
//
//import com.ticket.ticketmanagement.exception.FileUploadException;
//import jakarta.annotation.PostConstruct;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.S3Exception;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//public class S3Service {
//
//    private final S3Client s3Client;
//    private final ParameterStoreService parameterStoreService;
//
//    private String bucketName;
//
//    public S3Service(S3Client s3Client,
//                     ParameterStoreService parameterStoreService) {
//        this.s3Client = s3Client;
//        this.parameterStoreService = parameterStoreService;
//    }
//
//    @PostConstruct
//    public void init() {
//        bucketName = parameterStoreService.getParameter(
//                "/ticket-management/aws/bucket-name"
//        );
//    }
//
//    public String uploadFile(MultipartFile file) {
//
//        try {
//
//            String fileName =
//                    UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileName)
//                    .contentType(file.getContentType())
//                    .build();
//
//            s3Client.putObject(
//                    request,
//                    RequestBody.fromBytes(file.getBytes())
//            );
//
//
//            return s3Client.utilities()
//                    .getUrl(builder -> builder
//                            .bucket(bucketName)
//                            .key(fileName))
//                    .toExternalForm();
//
//        } catch (IOException e) {
//
//            throw new FileUploadException(
//                    "Failed to read uploaded file.",
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//
//        } catch (S3Exception e) {
//
//            throw new FileUploadException(
//                    "Failed to upload file to AWS S3.",
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }
//}