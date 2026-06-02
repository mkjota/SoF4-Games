package com.sofagames.backend.game.service;

import com.sofagames.backend.game.entity.*;
import com.sofagames.backend.game.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GameDataLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final CategoryRepository categoryRepository;
    private final DeveloperRepository developerRepository;
    private final PublishersRepository publisherRepository;
    private final ScreenshotRepository screenshotRepository;
    private final ObjectMapper objectMapper;

    public GameDataLoader(GameRepository gameRepository,
                          GenreRepository genreRepository,
                          CategoryRepository categoryRepository,
                          DeveloperRepository developerRepository,
                          PublishersRepository publisherRepository,
                          ScreenshotRepository screenshotRepository,
                          ObjectMapper objectMapper) {
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
        this.categoryRepository = categoryRepository;
        this.developerRepository = developerRepository;
        this.publisherRepository = publisherRepository;
        this.screenshotRepository = screenshotRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (gameRepository.count() > 0) {
            System.out.println("Database already contains games. Skipping data load.");
            return;
        }

        System.out.println("Loading game data from catalog_final.json...");
        try {
            ClassPathResource resource = new ClassPathResource("catalog_final.json");
            Map<String, Object> catalog = objectMapper.readValue(resource.getInputStream(), Map.class);

            for (Map.Entry<String, Object> entry : catalog.entrySet()) {
                String gameId = entry.getKey();
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> gameData = (Map<String, Object>) entry.getValue();

                    Game game = mapToGame(gameData);
                    Game savedGame = gameRepository.save(game);

                    if (gameData.containsKey("genres")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> genres = (List<Map<String, Object>>) gameData.get("genres");
                        for (Map<String, Object> genreData : genres) {
                            Integer id = parseIntSafe(genreData.get("id"));
                            String name = (String) (genreData.get("description") != null ? genreData.get("description") : genreData.get("name"));
                            Genre genre = genreRepository.findById(id)
                                    .orElseGet(() -> {
                                        Genre g = new Genre();
                                        g.setId(id);
                                        g.setName(name != null ? name : "Unknown");
                                        return genreRepository.save(g);
                                    });
                            savedGame.getGenres().add(genre);
                        }
                    }

                    if (gameData.containsKey("categories")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> categories = (List<Map<String, Object>>) gameData.get("categories");
                        for (Map<String, Object> catData : categories) {
                            Integer id = parseIntSafe(catData.get("id"));
                            String name = (String) (catData.get("description") != null ? catData.get("description") : catData.get("name"));
                            Category category = categoryRepository.findById(id)
                                    .orElseGet(() -> {
                                        Category c = new Category();
                                        c.setId(id);
                                        c.setName(name != null ? name : "Unknown");
                                        return categoryRepository.save(c);
                                    });
                            savedGame.getCategories().add(category);
                        }
                    }

                    processList(gameData, "developers", savedGame.getDevelopers(), developerRepository);
                    processList(gameData, "publishers", savedGame.getPublishers(), publisherRepository);


                    if (gameData.containsKey("screenshots")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> screenshots = (List<Map<String, Object>>) gameData.get("screenshots");
                        for (Map<String, Object> shotData : screenshots) {
                            Screenshot screenshot = new Screenshot();
                            screenshot.setGame(savedGame);
                            screenshot.setSteamId(parseIntSafe(shotData.get("id")));
                            screenshot.setPathThumbnail((String) shotData.get("path_thumbnail"));
                            screenshot.setPathFull((String) shotData.get("path_full"));
                            screenshot.setDisplayOrder(shotData.get("display_order") != null ? (Integer) shotData.get("display_order") : screenshot.getSteamId());
                            screenshotRepository.save(screenshot);
                        }
                    }

                    gameRepository.save(savedGame);
                } catch (Exception e) {
                    System.err.println("Error processing game with ID: " + gameId);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Game mapToGame(Map<String, Object> data) {
        Game game = new Game();
        game.setSteamAppId(parseIntSafe(data.get("steam_appid")));
        game.setName((String) data.get("name"));

        if (data.get("metacritic") instanceof Map) {
            Map<String, Object> meta = (Map<String, Object>) data.get("metacritic");
            game.setMetacriticScore(parseIntSafe(meta.get("score")));
        } else {
            game.setMetacriticScore(0);
        }

        if (data.get("recommendations") instanceof Map) {
            Map<String, Object> recs = (Map<String, Object>) data.get("recommendations");
            game.setRecommendationsTotal(parseIntSafe(recs.get("total")));
        } else {
            game.setRecommendationsTotal(0);
        }

        game.setShortDescription((String) data.get("short_description"));
        game.setHeaderImage((String) data.get("header_image"));
        game.setIsFree((Boolean) data.getOrDefault("is_free", false));

        return game;
    }

    private void processList(Map<String, Object> data, String key, Set set, Object repo) {
    }

    private int parseIntSafe(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) {
            try { return Integer.parseInt((String) obj); } catch (Exception e) { return 0; }
        }
        return 0;
    }
}