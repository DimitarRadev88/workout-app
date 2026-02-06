package com.dimitarrradev.exercisesApi.exercise.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value="exercises")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExercisePagedModel extends RepresentationModel<CollectionModel<ExerciseModel>> {
    private List<ExerciseModel> content;

}
