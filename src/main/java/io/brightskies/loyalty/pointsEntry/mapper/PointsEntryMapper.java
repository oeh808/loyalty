package io.brightskies.loyalty.pointsEntry.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.brightskies.loyalty.pointsEntry.dto.PointsEntryReadingDto;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;

@Component
public class PointsEntryMapper {
    // To Dto:
    public PointsEntryReadingDto toDto(PointsEntry pointsEntry) {
        PointsEntryReadingDto dto = new PointsEntryReadingDto(pointsEntry.isExpired(), pointsEntry.getNumOfPoints(),
                pointsEntry.getExpiryDate());

        return dto;
    }

    public List<PointsEntryReadingDto> toDto(List<PointsEntry> pointsEntries) {
        List<PointsEntryReadingDto> dtos = new ArrayList<>();
        for (PointsEntry pointsEntry : pointsEntries) {
            dtos.add(toDto(pointsEntry));
        }

        return dtos;
    }
}
