package com.sofagames.backend.game.repository;

import com.sofagames.backend.game.entity.Publishers;
import com.sofagames.backend.game.entity.Publishers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublishersRepository extends JpaRepository<Publishers, Long> {
    Optional<Publishers> findByName(String name);
}
