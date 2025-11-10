package com.ashtana.backend.Controller;

import com.ashtana.backend.Entity.Color;
import com.ashtana.backend.Service.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

    // ✅ Create Color
    @PostMapping
    public ResponseEntity<Color> createColor(@Valid @RequestBody Color color) {
        Color savedColor = colorService.createColor(color);
        return ResponseEntity.status(201).body(savedColor);
    }

    // ✅ Get All Colors
    @GetMapping
    public ResponseEntity<List<Color>> getAllColors() {
        List<Color> colors = colorService.getAllColors();
        return ResponseEntity.ok(colors);
    }

    // ✅ Get Color by ID
    @GetMapping("/{id}")
    public ResponseEntity<Color> getColorById(@PathVariable Long id) {
        Color color = colorService.getColorById(id);
        return ResponseEntity.ok(color);
    }

    // ✅ Update Color
    @PutMapping("/{id}")
    public ResponseEntity<Color> updateColor(
            @PathVariable Long id,
            @Valid @RequestBody Color updatedColor) {

        Color color = colorService.updateColor(id, updatedColor);
        return ResponseEntity.ok(color);
    }

    // ✅ Delete Color
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColor(@PathVariable Long id) {
        colorService.deleteColor(id);
        return ResponseEntity.noContent().build();
    }
}
