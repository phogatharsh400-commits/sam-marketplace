package com.sam.marketplace.benchmark;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benchmarks")
@Tag(name = "Price Benchmarks", description = "Mandi Price Benchmark and Trend Intelligence")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;
    private final MandiPriceSyncService mandiPriceSyncService;

    public BenchmarkController(BenchmarkService benchmarkService, MandiPriceSyncService mandiPriceSyncService) {
        this.benchmarkService = benchmarkService;
        this.mandiPriceSyncService = mandiPriceSyncService;
    }

    @PostMapping("/sync")
    @Operation(summary = "Trigger manual or automated APMC daily price sync")
    public ResponseEntity<ApiResponse<MandiPriceSyncService.SyncReport>> syncDailyMandiPrices() {
        MandiPriceSyncService.SyncReport report = mandiPriceSyncService.syncDailyPrices();
        return ResponseEntity.ok(ApiResponse.success(report, "Mandi benchmark prices synchronized successfully"));
    }

    @GetMapping("/sync-status")
    @Operation(summary = "Get daily APMC sync engine status")
    public ResponseEntity<ApiResponse<MandiPriceSyncService.SyncReport>> getSyncStatus() {
        return ResponseEntity.ok(ApiResponse.success(mandiPriceSyncService.getSyncStatus(), "Sync engine status retrieved"));
    }

    @GetMapping
    @Operation(summary = "Get all latest mandi price benchmarks")
    public ResponseEntity<ApiResponse<List<BenchmarkDto>>> getAllBenchmarks() {
        return ResponseEntity.ok(ApiResponse.success(benchmarkService.getAllLatestBenchmarks(), "Latest benchmarks retrieved"));
    }

    @GetMapping("/{crop}")
    @Operation(summary = "Get latest benchmark for specific crop")
    public ResponseEntity<ApiResponse<List<BenchmarkDto>>> getBenchmarksByCrop(@PathVariable CropType crop) {
        return ResponseEntity.ok(ApiResponse.success(benchmarkService.getBenchmarksByCrop(crop), "Crop benchmarks retrieved"));
    }

    @GetMapping("/{crop}/{district}")
    @Operation(summary = "Get latest benchmark for crop and district")
    public ResponseEntity<ApiResponse<BenchmarkDto>> getCropDistrictBenchmark(
            @PathVariable CropType crop, @PathVariable String district) {
        return ResponseEntity.ok(ApiResponse.success(benchmarkService.getLatestBenchmark(crop, district), "Benchmark retrieved"));
    }

    @GetMapping("/{crop}/trend")
    @Operation(summary = "Get 30-day historical price trend for crop")
    public ResponseEntity<ApiResponse<List<PriceTrendDto>>> getPriceTrend(
            @PathVariable CropType crop, @RequestParam(required = false) String district) {
        return ResponseEntity.ok(ApiResponse.success(benchmarkService.get30DayTrend(crop, district), "30-day price trend retrieved"));
    }

    @GetMapping("/{crop}/{district}/trends")
    @Operation(summary = "Get 30-day historical price trend for crop and district")
    public ResponseEntity<ApiResponse<List<PriceTrendDto>>> getPriceTrendByPath(
            @PathVariable CropType crop, @PathVariable String district) {
        return ResponseEntity.ok(ApiResponse.success(benchmarkService.get30DayTrend(crop, district), "30-day price trend retrieved"));
    }

    @GetMapping("/districts")
    @Operation(summary = "Get list of supported districts")
    public ResponseEntity<ApiResponse<List<String>>> getDistricts() {
        return ResponseEntity.ok(ApiResponse.success(benchmarkService.getSupportedDistricts(), "Districts list retrieved"));
    }
}
