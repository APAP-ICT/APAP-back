package com.example.apapbackend.Info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Info {

    @Column
    public LocalDateTime localDateTime;
    @Column
    public String label;
    @Column
    public String imageUrl;
    @Id
    @GeneratedValue
    private Long id;

    public Info(LocalDateTime localDateTime, String label, String imageUrl) {
        this.localDateTime = localDateTime;
        this.label = label;
        this.imageUrl = imageUrl;
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
}
