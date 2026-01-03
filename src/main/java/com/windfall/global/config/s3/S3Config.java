package com.windfall.global.config.s3;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
  @Value("${aws.s3.accessKey}")
  private String accessKey;

  @Value("${aws.s3.secretKey}")
  private String secretKey;

  @Bean
  public S3Client s3Client(){
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
    return S3Client.builder()
        .region(Region.AP_NORTHEAST_2)
        .credentialsProvider(provider)
        .build();
  }
}
