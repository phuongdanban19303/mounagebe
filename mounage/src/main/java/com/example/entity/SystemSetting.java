package com.example.entity;

import com.example.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "system_settings")
public class SystemSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id", nullable = false)
    private Integer id;

    @Column(name = "setting_key", nullable = false, length = 100)
    private String settingKey;

    @Lob
    @Column(name = "setting_value", nullable = false)
    private String settingValue;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @ColumnDefault("'string'")
    @Lob
    @Column(name = "data_type")
    private String dataType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}