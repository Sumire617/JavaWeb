package org.sumire.studyhardprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sumire.studyhardprogram.service.DataAnalysisService;

import java.time.LocalDate;
import java.util.Map;

/**
 * 数据分析控制器，提供各种数据分析接口
 */
@RestController
@RequestMapping("/api/data-analysis")
@CrossOrigin
public class DataAnalysisController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    /**
     * 获取特定时间段内的岗位数量统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param interval 统计间隔（day, week, month）
     * @return 时间段内的岗位数量统计数据
     */
    @GetMapping("/job-count")
    public ResponseEntity<Map<String, Object>> getJobCountByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "day") String interval) {
        
        Map<String, Object> data = dataAnalysisService.getJobCountByTimeRange(startDate, endDate, interval);
        return ResponseEntity.ok(data);
    }

    /**
     * 获取特定时间段内的用户活跃度统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param interval 统计间隔（day, week, month）
     * @return 时间段内的用户活跃度统计数据
     */
    @GetMapping("/user-activity")
    public ResponseEntity<Map<String, Object>> getUserActivityByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "day") String interval) {
        
        Map<String, Object> data = dataAnalysisService.getUserActivityByTimeRange(startDate, endDate, interval);
        return ResponseEntity.ok(data);
    }

    /**
     * 获取岗位状态统计
     * @return 各状态岗位数量统计
     */
    @GetMapping("/job-status")
    public ResponseEntity<Map<String, Object>> getJobStatusStats() {
        Map<String, Object> stats = dataAnalysisService.getJobStatusStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取岗位地区分布统计
     * @return 各地区岗位数量统计
     */
    @GetMapping("/location-distribution")
    public ResponseEntity<Map<String, Object>> getLocationDistribution() {
        Map<String, Object> stats = dataAnalysisService.getLocationDistribution();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取概览数据
     * @return 平台概览数据
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverviewData() {
        Map<String, Object> overviewData = dataAnalysisService.getOverviewData();
        return ResponseEntity.ok(overviewData);
    }
} 