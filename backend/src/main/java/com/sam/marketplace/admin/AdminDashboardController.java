package com.sam.marketplace.admin;

import com.sam.marketplace.audit.AuditLogDto;
import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.benchmark.BenchmarkDto;
import com.sam.marketplace.benchmark.BenchmarkService;
import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Admin Dashboard, Auditing, Reports and Price Benchmarking")
public class AdminDashboardController {

    private final AdminService adminService;
    private final AuditService auditService;
    private final BenchmarkService benchmarkService;

    public AdminDashboardController(AdminService adminService,
                                    AuditService auditService,
                                    BenchmarkService benchmarkService) {
        this.adminService = adminService;
        this.auditService = auditService;
        this.benchmarkService = benchmarkService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard high-level KPIs and metrics")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getDashboardStats() {
        AdminDashboardDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard metrics retrieved successfully"));
    }

    @GetMapping("/reports/summary")
    @Operation(summary = "Get system summary reports")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getReportsSummary() {
        AdminDashboardDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "System report summary retrieved"));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get system audit logs")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogDto>>> getAuditLogs(
            @PageableDefault(size = 30) Pageable pageable) {
        PageResponse<AuditLogDto> logs = auditService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success(logs, "Audit logs retrieved successfully"));
    }

    @PostMapping("/benchmarks")
    @Operation(summary = "Add or update mandi benchmark price record")
    public ResponseEntity<ApiResponse<BenchmarkDto>> createBenchmark(@RequestBody BenchmarkDto dto) {
        BenchmarkDto saved = benchmarkService.createOrUpdateBenchmark(dto);
        return ResponseEntity.ok(ApiResponse.success(saved, "Benchmark price record saved successfully"));
    }

    @PutMapping("/benchmarks/{id}")
    @Operation(summary = "Update mandi benchmark price record")
    public ResponseEntity<ApiResponse<BenchmarkDto>> updateBenchmark(
            @PathVariable java.util.UUID id,
            @RequestBody BenchmarkDto dto) {
        dto.setId(id);
        BenchmarkDto saved = benchmarkService.createOrUpdateBenchmark(dto);
        return ResponseEntity.ok(ApiResponse.success(saved, "Benchmark price record updated successfully"));
    }
}
