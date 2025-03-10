package org.sumire.studyhardprogram.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// 评价记录实体类
@Data
@Entity
@Table(name = "job_evaluations")
public class JobEvaluation {
    @Id
    private String evaluationId;

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer rating;
    private String content;
    private LocalDateTime evaluationTime;
    private Boolean isAnonymous;
}
