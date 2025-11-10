package com.ashtana.backend.Service;

import com.ashtana.backend.Entity.Color;
import com.ashtana.backend.Repository.ColorRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ColorService {

    private final ColorRepo colorRepository;

    // ✅ Create new color
    public Color createColor(Color color) {
        if (color.getColorName() == null || color.getColorCode() == null) {
            throw new IllegalArgumentException("Color name and color code cannot be null");
        }
        return colorRepository.save(color);
    }

    // ✅ Get all colors
    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    // ✅ Get color by ID
    public Color getColorById(Long id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Color not found with ID: " + id));
    }

    // ✅ Update color
    public Color updateColor(Long id, Color updatedColor) {
        Color existing = colorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Color not found with ID: " + id));

        if (updatedColor.getColorName() != null) {
            existing.setColorName(updatedColor.getColorName());
        }
        if (updatedColor.getColorCode() != null) {
            existing.setColorCode(updatedColor.getColorCode());
        }

        return colorRepository.save(existing);
    }

    // ✅ Delete color
    public void deleteColor(Long id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Color not found with ID: " + id));
        colorRepository.delete(color);
    }
}
