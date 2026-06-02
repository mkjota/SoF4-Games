package com.sofagames.backend.game.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Category {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private Set<Game> games = new HashSet<>();

    public Category(Integer id, String s) {
    }
}
