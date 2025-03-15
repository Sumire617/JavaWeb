package org.sumire.studyhardprogram.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * 数据分析服务接口，提供各种数据分析功能
 */
public interface DataAnalysisService {
    
    /**
     * 获取特定时间段内的岗位数量统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param interval 统计间隔（day, week, month）
     * @return 时间段内的岗位数量统计数据
     */
    Map<String, Object> getJobCountByTimeRange(LocalDate startDate, LocalDate endDate, String interval);
    
    /**
     * 获取特定时间段内的用户活跃度统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param interval 统计间隔（day, week, month）
     * @return 时间段内的用户活跃度统计数据
     */
    Map<String, Object> getUserActivityByTimeRange(LocalDate startDate, LocalDate endDate, String interval);
    
    /**
     * 获取岗位状态统计
     * @return 各状态岗位数量统计
     */
    Map<String, Object> getJobStatusStats();
    
    /**
     * 获取岗位地区分布统计
     * @return 各地区岗位数量统计
     */
    Map<String, Object> getLocationDistribution();
    
    /**
     * 获取概览数据
     * @return 平台概览数据
     */
    Map<String, Object> getOverviewData();
} 