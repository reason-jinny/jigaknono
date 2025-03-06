package com.kt.jigaknono.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AdditionalSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int entranceTime = 0;

    private int officeTime = 0;

    @ManyToOne
    @JoinColumn(name = "user_settings_id")
    private UserSettings userSettings;
}