package com.example.entity.user;

import com.example.entity.AuditLog;
import com.example.entity.LoginHistory;
import com.example.entity.SystemSetting;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

//    @Column(name = "salt", nullable = false)
//    private String salt;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_login")
    private Instant lastLogin;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "user")
    private Set<AuditLog> auditLogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<LoginHistory> loginHistories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "updatedBy")
    private Set<SystemSetting> systemSettings = new LinkedHashSet<>();

}