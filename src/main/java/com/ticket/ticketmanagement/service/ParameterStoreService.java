//package com.ticket.ticketmanagement.service;
//
//import com.ticket.ticketmanagement.exception.BaseException;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.ssm.SsmClient;
//import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
//import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
//import software.amazon.awssdk.services.ssm.model.SsmException;
//
//@Service
//public class ParameterStoreService {
//
//    private final SsmClient ssmClient;
//
//    public ParameterStoreService(SsmClient ssmClient) {
//        this.ssmClient = ssmClient;
//    }
//
//    public String getParameter(String parameterName) {
//
//        try {
//
//            GetParameterRequest request = GetParameterRequest.builder()
//                    .name(parameterName)
//                    .withDecryption(true)
//                    .build();
//
//            GetParameterResponse response =
//                    ssmClient.getParameter(request);
//
//            return response.parameter().value();
//
//        } catch (SsmException ex) {
//
//            throw new BaseException(
//                    "Failed to retrieve parameter: " + parameterName,
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }
//}