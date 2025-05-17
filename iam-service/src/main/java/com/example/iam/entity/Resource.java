package com.example.iam.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;


    @Column(name = "http_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private HttpMethod method;

    @Column(name = "path", nullable = false)
    private String path;


    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
    }
} 