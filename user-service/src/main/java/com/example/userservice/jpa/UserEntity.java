package com.example.userservice.jpa;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "T_SC_USERS")
public class UserEntity {
    @Id
    @SequenceGenerator(name = "S_SC_USER_SEQ", sequenceName = "S_SC_USER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_SC_USER_SEQ")
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false) // , unique = true)
    private String encryptedPwd;
}
