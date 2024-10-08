package com.example.apapbackend.domain.Info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
public class Info {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String cameraName;
    @Column
    public LocalDateTime localDateTime;
    @Column
    public String label;
    @Column
    public String imageUrl;
    @Column
    public String description;

    public Info(String cameraName, LocalDateTime localDateTime, String label, String imageUrl,
        String description) {
        this.cameraName = cameraName;
        this.localDateTime = localDateTime;
        this.label = label;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public Info() {

    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCameraName() {
        return cameraName;
    }

    public String getDescription() {
        return description;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
