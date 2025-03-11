package com.kt.jigaknono.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;  // "add", "update", "other" 값을 직접 저장

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column
    private Boolean isResolved = false;

    @Column
    private String adminComment;
    
}