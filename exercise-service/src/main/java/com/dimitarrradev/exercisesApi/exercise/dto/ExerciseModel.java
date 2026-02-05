package com.dimitarrradev.exercisesApi.exercise.dto;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.image.dto.ImageUrlModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value="exercise")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExerciseModel extends RepresentationModel<ExerciseModel> {
        private Long id;
        private String name;
        private Complexity complexity;
        private String description;
        private MovementType movementType;
        private TargetBodyPart targetBodyPart;
        private List<ImageUrlModel> imageUrls;
        private Boolean approved;
        private String addedBy;
}
