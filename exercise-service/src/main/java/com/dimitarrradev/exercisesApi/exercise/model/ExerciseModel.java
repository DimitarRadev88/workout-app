package com.dimitarrradev.exercisesApi.exercise.model;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value="exercise")
public class ExerciseModel extends RepresentationModel<ExerciseModel> {
        private Long id;
        private String name;
        private Complexity complexity;
        private String description;
        private MovementType movementType;
        private TargetBodyPart targetBodyPart;
}
