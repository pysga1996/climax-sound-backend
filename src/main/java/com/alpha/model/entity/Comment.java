package com.alpha.model.entity;

import com.alpha.model.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_id_gen")
    @SequenceGenerator(name = "comment_id_gen", sequenceName = "comment_id_seq", allocationSize = 1)
    private Long id;

    //    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 500)
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime localDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Song song;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo userInfo;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
