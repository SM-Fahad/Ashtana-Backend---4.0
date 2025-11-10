package com.ashtana.backend.Controller;

import com.ashtana.backend.Entity.Size;
import com.ashtana.backend.Service.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService sizeService;

    // ✅ Create Size
    @PostMapping
    public ResponseEntity<Size> createSize(@Valid @RequestBody Size size) {
        Size savedSize = sizeService.createSize(size);
        return ResponseEntity.status(201).body(savedSize);
    }

    // ✅ Get All Sizes
    @GetMapping
    public ResponseEntity<List<Size>> getAllSizes() {
        List<Size> sizes = sizeService.getAllSizes();
        return ResponseEntity.ok(sizes);
    }

    // ✅ Get Size by ID
    @GetMapping("/{id}")
    public ResponseEntity<Size> getSizeById(@PathVariable Long id) {
        Size size = sizeService.getSizeById(id);
        return ResponseEntity.ok(size);
    }

    // ✅ Update Size
    @PutMapping("/{id}")
    public ResponseEntity<Size> updateSize(
            @PathVariable Long id,
            @Valid @RequestBody Size updatedSize) {

        Size size = sizeService.updateSize(id, updatedSize);
        return ResponseEntity.ok(size);
    }

    // ✅ Delete Size
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }
}
