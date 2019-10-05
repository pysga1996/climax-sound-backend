package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
@RequiredArgsConstructor
class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 5)
    private final Integer rating;

    @JsonManagedReference(value = "song-comment")
    @ManyToOne(fetch = FetchType.LAZY)
    private final Song song;

    @JsonManagedReference(value = "user-comment")
    @ManyToOne(fetch = FetchType.LAZY)
    private final User user;
}
