package com.example.iam.mapper;

import com.example.iam.dto.ClientResponse;
import com.example.iam.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "authorizationGrantTypes", expression = "java(client.getAuthorizationGrantTypes())")
    @Mapping(target = "clientAuthenticationMethods", expression = "java(client.getClientAuthenticationMethods())")
    ClientResponse toClientResponse(Client client);
} 