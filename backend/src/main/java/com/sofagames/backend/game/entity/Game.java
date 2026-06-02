package com.sofagames.backend.game.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "steam_appid", nullable = false, unique = true)
    private Integer steamAppId;

    @Column(nullable = false)
    private String name;

    @Column(name = "collection")
    private String collection;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "detailed_description", columnDefinition = "TEXT")
    private String detailedDescription;

    @Column(name = "header_image", columnDefinition = "TEXT")
    private String headerImage;

    @Column(name = "capsule_image", columnDefinition = "TEXT")
    private String capsuleImage;

    @Column(name = "background_raw", columnDefinition = "TEXT")
    private String backgroundRaw;

    private String website;

    @Column(name = "is_free", nullable = false)
    @Builder.Default
    private Boolean isFree = false;

    @Column(name = "coming_soon")
    @Builder.Default
    private Boolean comingSoon = false;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "price_initial", nullable = false)
    @Builder.Default
    private Integer priceInitial = 0;

    @Column(name = "price_final", nullable = false)
    @Builder.Default
    private Integer priceFinal = 0;

    @Column(name = "discount_percent")
    @Builder.Default
    private Integer discountPercent = 0;

    @Column(name = "currency")
    private String currency;

    @Column(name = "required_age")
    private Integer requiredAge = 0;

    @Column(name = "controller_support")
    private String controllerSupport;

    @Column(name = "supported_languages", columnDefinition = "TEXT")
    private String supportedLanguages;

    @Column(name = "recommendations_total")
    private Integer recommendationsTotal = 0;

    @Column(name = "metacritic_score")
    private Integer metacriticScore = 0;

    @Column(name = "achievements_total")
    private Integer achievementsTotal = 0;

    @Column(name = "system_requirements", columnDefinition = "TEXT")
    private String systemRequirements;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_genres",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_categories",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_developers",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id"))
    @Builder.Default
    private Set<Developer> developers = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "game_publishers",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "publisher_id"))
    @Builder.Default
    private Set<Publishers> publishers = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private Set<Screenshot> screenshots = new HashSet<>();
}