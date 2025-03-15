package org.sumire.studyhardprogram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sumire.studyhardprogram.model.JobPost;
import org.sumire.studyhardprogram.repository.JobApplicationRepository;
import org.sumire.studyhardprogram.repository.JobPostRepository;
import org.sumire.studyhardprogram.repository.UserRepository;
import org.sumire.studyhardprogram.service.DataAnalysisService;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String, Object> getJobCountByTimeRange(LocalDate startDate, LocalDate endDate, String interval) {
        // 获取所有职位
        List<JobPost> allJobs = jobPostRepository.findAll();
        
        // 按照postedAt日期进行过滤
        List<JobPost> filteredJobs = allJobs.stream()
                .filter(job -> {
                    LocalDate postedDate = job.getPostedAt()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !postedDate.isBefore(startDate) && !postedDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
        
        // 按照时间间隔分组统计
        Map<String, Long> countByPeriod = new LinkedHashMap<>();
        List<String> timeLabels = new ArrayList<>();
        
        // 根据interval参数决定统计方式
        switch (interval) {
            case "day":
                // 按天统计
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    timeLabels.add(dateStr);
                    
                    final LocalDate currentDate = date;
                    long count = filteredJobs.stream()
                            .filter(job -> {
                                LocalDate postedDate = job.getPostedAt()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                return postedDate.isEqual(currentDate);
                            })
                            .count();
                    
                    countByPeriod.put(dateStr, count);
                }
                break;
                
            case "week":
                // 按周统计，从周一开始
                LocalDate weekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate weekEnd = weekStart.plusDays(6);
                
                while (!weekStart.isAfter(endDate)) {
                    // 调整weekEnd不超过查询的endDate
                    if (weekEnd.isAfter(endDate)) {
                        weekEnd = endDate;
                    }
                    
                    String weekLabel = weekStart.format(DateTimeFormatter.ISO_LOCAL_DATE) + " to " + 
                                       weekEnd.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    timeLabels.add(weekLabel);
                    
                    final LocalDate currentWeekStart = weekStart;
                    final LocalDate currentWeekEnd = weekEnd;
                    
                    long count = filteredJobs.stream()
                            .filter(job -> {
                                LocalDate postedDate = job.getPostedAt()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                return !postedDate.isBefore(currentWeekStart) && !postedDate.isAfter(currentWeekEnd);
                            })
                            .count();
                    
                    countByPeriod.put(weekLabel, count);
                    
                    // 移动到下一周
                    weekStart = weekStart.plusWeeks(1);
                    weekEnd = weekStart.plusDays(6);
                }
                break;
                
            case "month":
                // 按月统计
                LocalDate monthStart = startDate.withDayOfMonth(1);
                LocalDate monthEnd;
                
                while (!monthStart.isAfter(endDate)) {
                    monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());
                    
                    // 调整monthEnd不超过查询的endDate
                    if (monthEnd.isAfter(endDate)) {
                        monthEnd = endDate;
                    }
                    
                    String monthLabel = monthStart.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    timeLabels.add(monthLabel);
                    
                    final LocalDate currentMonthStart = monthStart;
                    final LocalDate currentMonthEnd = monthEnd;
                    
                    long count = filteredJobs.stream()
                            .filter(job -> {
                                LocalDate postedDate = job.getPostedAt()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                return !postedDate.isBefore(currentMonthStart) && !postedDate.isAfter(currentMonthEnd);
                            })
                            .count();
                    
                    countByPeriod.put(monthLabel, count);
                    
                    // 移动到下一月
                    monthStart = monthStart.plusMonths(1);
                }
                break;
                
            default:
                // 默认按天统计
                return getJobCountByTimeRange(startDate, endDate, "day");
        }
        
        // 准备返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("labels", timeLabels);
        result.put("data", countByPeriod.values().toArray());
        result.put("total", filteredJobs.size());
        
        return result;
    }

    @Override
    public Map<String, Object> getUserActivityByTimeRange(LocalDate startDate, LocalDate endDate, String interval) {
        // 这里实现用户活跃度统计，例如通过JobApplication表统计用户申请岗位的活跃情况
        // 由于相似度较高，此处实现方式类似于getJobCountByTimeRange
        // 代码实现略...
        
        // 示例返回数据
        Map<String, Object> result = new HashMap<>();
        // 设置模拟数据
        List<String> timeLabels = new ArrayList<>();
        List<Integer> activityData = new ArrayList<>();
        
        // 按照interval的不同，生成不同粒度的时间标签
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if ("day".equals(interval)) {
                timeLabels.add(current.format(DateTimeFormatter.ISO_LOCAL_DATE));
                current = current.plusDays(1);
            } else if ("week".equals(interval)) {
                // 生成周标签
                LocalDate weekEnd = current.plusDays(6);
                if (weekEnd.isAfter(endDate)) weekEnd = endDate;
                timeLabels.add(current.format(DateTimeFormatter.ISO_LOCAL_DATE) + " to " + weekEnd.format(DateTimeFormatter.ISO_LOCAL_DATE));
                current = current.plusWeeks(1);
            } else if ("month".equals(interval)) {
                // 生成月标签
                timeLabels.add(current.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                current = current.plusMonths(1);
            }
            
            // 生成随机活跃度数据（实际项目中应替换为真实数据）
            activityData.add(new Random().nextInt(50) + 50);
        }
        
        result.put("labels", timeLabels);
        result.put("data", activityData);
        
        return result;
    }

    @Override
    public Map<String, Object> getJobStatusStats() {
        // 获取不同状态的岗位数量
        long pendingCount = jobPostRepository.countByStatus("PENDING");
        long approvedCount = jobPostRepository.countByStatus("APPROVED");
        long rejectedCount = jobPostRepository.countByStatus("REJECTED");
        long totalCount = pendingCount + approvedCount + rejectedCount;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", pendingCount);
        stats.put("approved", approvedCount);
        stats.put("rejected", rejectedCount);
        stats.put("total", totalCount);
        
        // 计算各状态的占比
        if (totalCount > 0) {
            stats.put("pendingRate", Math.round(pendingCount * 100.0 / totalCount));
            stats.put("approvedRate", Math.round(approvedCount * 100.0 / totalCount));
            stats.put("rejectedRate", Math.round(rejectedCount * 100.0 / totalCount));
        } else {
            stats.put("pendingRate", 0);
            stats.put("approvedRate", 0);
            stats.put("rejectedRate", 0);
        }
        
        return stats;
    }

    @Override
    public Map<String, Object> getLocationDistribution() {
        // 获取所有已审核通过的岗位
        List<JobPost> approvedJobs = jobPostRepository.findAll().stream()
                .filter(job -> "APPROVED".equals(job.getStatus()))
                .collect(Collectors.toList());
        
        // 按地区分组统计
        Map<String, Long> locationStats = approvedJobs.stream()
                .collect(Collectors.groupingBy(
                        JobPost::getLocation,
                        Collectors.counting()
                ));
        
        Map<String, Object> result = new HashMap<>();
        result.put("locations", locationStats.keySet().toArray());
        result.put("counts", locationStats.values().toArray());
        
        return result;
    }

    @Override
    public Map<String, Object> getOverviewData() {
        // 获取平台概览数据
        long totalJobs = jobPostRepository.count();
        long activeJobs = jobPostRepository.countByStatus("APPROVED");
        long totalApplications = jobApplicationRepository.count();
        long totalUsers = userRepository.count();
        
        Map<String, Object> overviewData = new HashMap<>();
        overviewData.put("totalJobs", totalJobs);
        overviewData.put("activeJobs", activeJobs);
        overviewData.put("totalApplications", totalApplications);
        overviewData.put("totalUsers", totalUsers);
        
        return overviewData;
    }
} 