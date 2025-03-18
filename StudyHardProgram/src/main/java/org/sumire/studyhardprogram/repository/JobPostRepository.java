package org.sumire.studyhardprogram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sumire.studyhardprogram.model.JobPost;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, String>, JpaSpecificationExecutor<JobPost> {
    Optional<JobPost> findByJobTitle(String jobTitle);
    Page<JobPost> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
    
    @Query("SELECT COUNT(DISTINCT j.location) FROM JobPost j WHERE j.status = 'APPROVED'")
    long countDistinctLocations();
    
    @Query("SELECT COUNT(DISTINCT j.employerId) FROM JobPost j WHERE j.status = 'APPROVED'")
    long countDistinctEmployers();
    
    /**
     * 获取指定时间范围内创建的岗位
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 指定时间范围内创建的岗位列表
     */
    List<JobPost> findByPostedAtBetween(Instant startDate, Instant endDate);
    
    /**
     * 获取指定时间范围内创建的岗位数量
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 指定时间范围内创建的岗位数量
     */
    @Query("SELECT COUNT(j) FROM JobPost j WHERE j.postedAt BETWEEN :startDate AND :endDate")
    long countByPostedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    /**
     * 按日期统计岗位数量
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 每天的岗位数量统计
     */
    @Query("SELECT DATE(j.postedAt) as date, COUNT(j) as count FROM JobPost j " +
           "WHERE j.postedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(j.postedAt) ORDER BY date")
    List<Object[]> countJobsByDay(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    /**
     * 统计各地区的岗位数量
     * @return 各地区的岗位数量
     */
    @Query("SELECT j.location, COUNT(j) FROM JobPost j WHERE j.status = 'APPROVED' GROUP BY j.location ORDER BY COUNT(j) DESC")
    List<Object[]> countJobsByLocation();

    /**
     * 统计各工作类型的岗位数量
     * @return 各工作类型的岗位数量
     */
    @Query("SELECT j.jobType, COUNT(j) FROM JobPost j WHERE j.status = 'APPROVED' GROUP BY j.jobType ORDER BY COUNT(j) DESC")
    List<Object[]> countJobsByType();

    /**
     * 获取所有可用的工作类型
     * @return 所有非空的工作类型列表
     */
    @Query("SELECT DISTINCT j.jobType FROM JobPost j WHERE j.jobType IS NOT NULL AND j.jobType <> '' AND j.status = 'APPROVED'")
    List<String> findAllJobTypes();

    /**
     * 根据工作类型查找岗位
     * @param jobType 工作类型
     * @param pageable 分页参数
     * @return 指定工作类型的岗位
     */
    Page<JobPost> findByJobTypeAndStatus(String jobType, String status, Pageable pageable);

    /**
     * 根据关键词获取搜索建议
     * @param keyword 用户输入的关键词
     * @return 匹配的职位标题、位置和要求列表
     */
    @Query("SELECT DISTINCT j.jobTitle FROM JobPost j WHERE j.status = 'APPROVED' AND LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY j.jobTitle")
    List<String> findJobTitleSuggestions(@Param("keyword") String keyword);

    /**
     * 根据关键词获取位置建议
     * @param keyword 用户输入的关键词
     * @return 匹配的位置列表
     */
    @Query("SELECT DISTINCT j.location FROM JobPost j WHERE j.status = 'APPROVED' AND LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY j.location")
    List<String> findLocationSuggestions(@Param("keyword") String keyword);

    /**
     * 根据关键词获取工作类型建议
     * @param keyword 用户输入的关键词
     * @return 匹配的工作类型列表
     */
    @Query("SELECT DISTINCT j.jobType FROM JobPost j WHERE j.status = 'APPROVED' AND j.jobType IS NOT NULL AND LOWER(j.jobType) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY j.jobType")
    List<String> findJobTypeSuggestions(@Param("keyword") String keyword);
}
