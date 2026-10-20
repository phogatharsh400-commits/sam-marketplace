package com.sam.marketplace.benchmark;

import com.sam.marketplace.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/msp")
@Tag(name = "MSP", description = "Minimum Support Price Data")
public class MspController {

    private final BenchmarkService benchmarkService;

    public MspController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @GetMapping
    @Operation(summary = "Get official government MSP (Minimum Support Price) values")
    public ResponseEntity<ApiResponse<List<MspDto>>> getMspValues() {
        return ResponseEntity.ok(ApiResponse.success(benchmarkService.getMspList(), "MSP list retrieved"));
    }
}
