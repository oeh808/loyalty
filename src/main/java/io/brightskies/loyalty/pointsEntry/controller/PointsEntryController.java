package io.brightskies.loyalty.pointsEntry.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.brightskies.loyalty.pointsEntry.dto.PointsEntryReadingDto;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.mapper.PointsEntryMapper;
import io.brightskies.loyalty.pointsEntry.service.PointsEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Tag(name = "Points Entries", description = "Controller for handling mappings for points entries")
@RequestMapping("/points")
public class PointsEntryController {
    private PointsEntryService pointsEntryService;
    private PointsEntryMapper pointsEntryMapper;

    public PointsEntryController(PointsEntryService pointsEntryService, PointsEntryMapper pointsEntryMapper) {
        this.pointsEntryService = pointsEntryService;
        this.pointsEntryMapper = pointsEntryMapper;
    }

    @Operation(description = "GET endpoint for retrieving a customer's points entries that are close to expiring" +
            "\n\n Returns a list of points entries as a list of PointsEntryReadingDto.", summary = "Get Customer's Soon to Expire Points")
    @GetMapping("/{phoneNumber}")
    public List<PointsEntryReadingDto> getSoonToExpirePointEntries(
            @Parameter(in = ParameterIn.PATH, name = "phoneNumber", description = "Phone Number") @PathVariable String phoneNumber) {
        List<PointsEntry> pointsEntriesSoonToExpire = pointsEntryService.getSoonToExpirePointsEntries(phoneNumber);

        return pointsEntryMapper.toDto(pointsEntriesSoonToExpire);
    }

}
