package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author Achintha Kalunayaka
 * @since 5/2/2025
 */

@Entity
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor
@Data
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private String role;
}
