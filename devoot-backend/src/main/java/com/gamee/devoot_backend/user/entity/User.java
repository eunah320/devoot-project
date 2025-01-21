package com.gamee.devoot_backend.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uid;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String profileId;

    private String nickname;

    @Column(columnDefinition = "JSON")
    private String links;

    private Boolean isPublic = true;

    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();
}
