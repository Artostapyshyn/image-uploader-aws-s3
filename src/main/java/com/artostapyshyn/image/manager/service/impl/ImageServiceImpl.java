package com.artostapyshyn.image.manager.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.artostapyshyn.image.manager.service.ImageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ImageServiceImpl implements ImageService {

    private AmazonS3 s3client;

    private AmazonRekognition rekognitionClient;

    @Value("${aws.endpointUrl}")
    private String endpointUrl;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @PostConstruct
    private void initializeAmazon() {

        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = AmazonS3Client.builder()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        this.rekognitionClient = AmazonRekognitionClient.builder()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

    }

    @Override
    public void uploadFile(MultipartFile multipartFile) {

        try {
            File file = convertMultiPartToFile(multipartFile);
            uploadFileTos3bucket(multipartFile.getOriginalFilename(), file);

        } catch (IOException e) {
            throw new RuntimeException("Failed to convert multipart file to file", e);
        }
    }

    @Override
    public List<String> searchImages(String query) {
        List<String> imageUrls = new ArrayList<>();

        ListObjectsV2Result result = s3client.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();

        for (S3ObjectSummary objectSummary : objects) {
            String photo = objectSummary.getKey();

            DetectLabelsRequest request = new DetectLabelsRequest()
                    .withImage(new Image().withS3Object(new com.amazonaws.services.rekognition.model.S3Object()
                            .withName(photo).withBucket(bucketName)))
                    .withMaxLabels(10)
                    .withMinConfidence(75F);

            try {
                DetectLabelsResult resultLabels = rekognitionClient.detectLabels(request);
                List<Label> labels = resultLabels.getLabels();

                for (Label label : labels) {
                    if (label.getName().toLowerCase().contains(query.toLowerCase())) {
                        String imageUrl = endpointUrl + "/" + bucketName + "/" + photo;
                        imageUrls.add(imageUrl);
                        break;
                    }
                }
            } catch (AmazonRekognitionException e) {
                throw new RuntimeException("Failed to process image labels with Amazon Rekognition", e);
            }
        }

        return imageUrls;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private void uploadFileTos3bucket(String fileName, File file) {

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "text");
            request.setCannedAcl(CannedAccessControlList.PublicRead);
            request.setMetadata(metadata);
            s3client.putObject(request);

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3 bucket.", e);
        }
    }
}