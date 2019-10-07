package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 5)
    private String content;

    @JsonManagedReference(value = "song-comment")
    @ManyToOne(fetch = FetchType.LAZY)
    private Song song;

    @JsonManagedReference(value = "user-comment")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Comment(String content) {
        this.content = content;
    }
}
