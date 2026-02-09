package com.dimitarrradev.exercisesApi.exercise.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value="image")
public class ImageUrlModel extends RepresentationModel<ImageUrlModel> {
    private Long id;
    private String url;
}
