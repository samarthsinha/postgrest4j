package me.samarthsinha.postgrest4j.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
    value = {"createdAt", "updatedAt","updatedBy"},
    allowGetters = true
)
@Getter
@Setter
public abstract class AuditModel implements Serializable {


  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "_created_at", nullable = false, updatable = false)
  @CreatedDate
  private Date createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "_updated_at", nullable = false)
  @LastModifiedDate
  private Date updatedAt;

  @Column(name = "_updated_by", nullable = true)
  @LastModifiedBy
  private String updatedBy;

}