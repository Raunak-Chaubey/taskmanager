package com.raunak.taskmanager.service;

import com.raunak.taskmanager.entity.Label;
import com.raunak.taskmanager.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;

    @Transactional
    public Set<Label> resolveLabels(Set<String> labelNames) {
        if (labelNames == null || labelNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> normalizedNames = labelNames.stream()
                .map(name -> name.trim().toLowerCase())
                .filter(name -> !name.isBlank())
                .collect(Collectors.toSet());

        Set<Label> existing = labelRepository.findByNameInIgnoreCase(normalizedNames);
        Set<String> existingNames = existing.stream()
                .map(label -> label.getName().toLowerCase())
                .collect(Collectors.toSet());

        Set<Label> resolved = new HashSet<>(existing);

        for (String name : normalizedNames) {
            if (!existingNames.contains(name)) {
                resolved.add(labelRepository.save(Label.builder().name(name).build()));
            }
        }

        return resolved;
    }
}
