package com.ashtana.backend.Service;

import com.ashtana.backend.Entity.Size;
import com.ashtana.backend.Repository.SizeRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepo sizeRepository;

    // ✅ Create new size
    public Size createSize(Size size) {
        return sizeRepository.save(size);
    }

    // ✅ Get all sizes
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    // ✅ Get size by ID
    public Size getSizeById(Long id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Size not found with ID: " + id));
    }

    // ✅ Update existing size
    public Size updateSize(Long id, Size updatedSize) {
        Size existingSize = getSizeById(id);
        existingSize.setSizeName(updatedSize.getSizeName());
        return sizeRepository.save(existingSize);
    }

    // ✅ Delete size
    public void deleteSize(Long id) {
        Size existingSize = getSizeById(id);
        sizeRepository.delete(existingSize);
    }
}
