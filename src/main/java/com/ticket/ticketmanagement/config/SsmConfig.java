//package com.ticket.ticketmanagement.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.ssm.SsmClient;
//
//@Configuration
//public class SsmConfig {
//
//    @Bean
//    public SsmClient ssmClient() {
//
//        return SsmClient.builder()
//                .region(Region.AP_SOUTH_1)
//                .credentialsProvider(DefaultCredentialsProvider.create())
//                .build();
//    }
//}