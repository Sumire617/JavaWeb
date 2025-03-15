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
}
