package com.raunak.taskmanager.repository;

import com.raunak.taskmanager.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByNameIgnoreCase(String name);

    Set<Label> findByNameInIgnoreCase(Set<String> names);
}
