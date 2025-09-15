package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.ImageUrl;
import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseRepository;
import com.dimitarrradev.workoutScheduler.exercise.dao.ImageUrlRepository;
import com.dimitarrradev.workoutScheduler.exercise.dto.*;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseFindBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ImageUrlViewModel;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ImageUrlRepository imageUrlRepository;

    public ExerciseService(ExerciseRepository exerciseRepository, ImageUrlRepository imageUrlRepository) {
        this.exerciseRepository = exerciseRepository;
        this.imageUrlRepository = imageUrlRepository;
    }

    public void addExerciseForReview(ExerciseAddBindingModel exerciseAdd) {
        if (exerciseRepository.existsExerciseByName(exerciseAdd.exerciseName())) {
            throw new IllegalArgumentException("Exercise already exists");
        }

        Exercise exercise = new Exercise();
        exercise.setName(exerciseAdd.exerciseName());
        exercise.setDescription(exerciseAdd.description());
        exercise.setTargetBodyPart(exerciseAdd.bodyPart());
        exercise.setApproved(false);
        exercise.setAddedBy(exerciseAdd.addedBy());
        exercise.setComplexity(exerciseAdd.complexity());
        exercise.setMovementType(exerciseAdd.movementType());

        exerciseRepository.save(exercise);
    }

    public long getExercisesForReviewCount() {
        return exerciseRepository.countAllByApprovedFalse();
    }

    public PageAndExerciseReviewServiceView getPaginatedAndSortedDataAndExerciseActiveFalse(int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<ExerciseForReviewViewModel> page = exerciseRepository
                .findAllByApprovedIsAndNameContainingIgnoreCase(pageable, false, "")
                .map(exercise -> new ExerciseForReviewViewModel(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getDescription(),
                        exercise.getComplexity(),
                        exercise.getMovementType(),
                        exercise.getAddedBy()
                ));

        return new PageAndExerciseReviewServiceView(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                String.format("Showing %d to %d of %d exercises",
                        page.getTotalElements() == 0 ? 0 : (pageNumber - 1) * pageSize + 1,
                        pageNumber < page.getTotalPages() ? (long) pageNumber * pageSize : page.getTotalElements(),
                        page.getTotalElements()
                ),
                List.of(5, 10, 25, 50)
        );
    }

    public void approveExercise(Long id) {
        Optional<Exercise> exerciseOptional = exerciseRepository.findById(id);

        if (exerciseOptional.isEmpty()) {
            throw new IllegalArgumentException("Exercise not found");
        }

        exerciseOptional.ifPresent(exercise -> {
            exercise.setApproved(true);
            exerciseRepository.save(exercise);
        });
    }

    public void deleteExercise(Long id) {
        Optional<Exercise> exerciseOptional = exerciseRepository.findById(id);

        if (exerciseOptional.isEmpty()) {
            throw new IllegalArgumentException("Exercise not found");
        }

        exerciseOptional.ifPresent(exerciseRepository::delete);
    }

    public PageAndExerciseFindServiceView getPaginatedAndSortedDataAndExerciseActiveTrue(ExerciseFindBindingModel exerciseFind, int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<ExerciseFindViewModel> page = null;

        TargetBodyPart targetBodyPart = exerciseFind.targetBodyPart() == null ? TargetBodyPart.ALL : exerciseFind.targetBodyPart();

        Complexity complexity = exerciseFind.complexity() == null ? Complexity.ALL : exerciseFind.complexity();

        MovementType movementType = exerciseFind.movementType() == null ? MovementType.All : exerciseFind.movementType();

        if (!targetBodyPart.equals(TargetBodyPart.ALL)) {
            if (!complexity.equals(Complexity.ALL) && !movementType.equals(MovementType.All)) {
                page = exerciseRepository
                        .findAllByApprovedTrueAndTargetBodyPartAndComplexityAndMovementType(
                                pageable,
                                targetBodyPart,
                                complexity,
                                movementType
                        )
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getMovementType()
                        ));
            } else if (!complexity.equals(Complexity.ALL)) {
                page = exerciseRepository
                        .findAllByApprovedTrueAndTargetBodyPartAndComplexity(
                                pageable,
                                targetBodyPart,
                                complexity
                        )
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getMovementType()
                        ));
            } else if (!movementType.equals(MovementType.All)) {
                page = exerciseRepository
                        .findAllByApprovedTrueAndTargetBodyPartAndMovementType(
                                pageable,
                                targetBodyPart,
                                movementType
                        )
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getMovementType()
                        ));
            } else {
                page = exerciseRepository
                        .findAllByApprovedTrueAndTargetBodyPart(
                                pageable,
                                exerciseFind.targetBodyPart()
                        )
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getMovementType()
                        ));
            }
        } else {
            if (!complexity.equals(Complexity.ALL) && !movementType.equals(MovementType.All)) {
                page = exerciseRepository
                        .findAllByApprovedTrueAndComplexityAndMovementType(
                                pageable,
                                complexity,
                                movementType
                        )
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getMovementType()
                        ));
            } else if (!complexity.equals(Complexity.ALL)) {
                page = exerciseRepository
                        .findAllByApprovedTrueAndComplexity(
                                pageable,
                                complexity
                        )
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getMovementType()
                        ));

            } else if (!movementType.equals(MovementType.All)) {
                page = exerciseRepository
                        .findAllByApprovedTrueAndMovementType(
                                pageable,
                                movementType
                        )
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getMovementType()
                        ));

            } else {
                page = exerciseRepository
                        .findAllByApprovedTrue(
                                pageable
                        )
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity(),
                                exercise.getMovementType()
                        ));
            }

        }

        return new PageAndExerciseFindServiceView(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                String.format("Showing %d to %d of %d exercises",
                        page.getTotalElements() == 0 ? 0 : (pageNumber - 1) * pageSize + 1,
                        pageNumber < page.getTotalPages() ? (long) pageNumber * pageSize : page.getTotalElements(),
                        page.getTotalElements()
                ),
                List.of(5, 10, 25, 50)
        );
    }

    @Transactional
    public ExerciseViewModel getExerciseView(Long id) {
        Exercise exercise = exerciseRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        return new ExerciseViewModel(
                exercise.getName(),
                exercise.getComplexity().getName(),
                exercise.getMovementType().getName(),
                exercise.getDescription(),
                exercise.getImageURLs().stream()
                        .map(imageUrl -> new com.dimitarrradev.workoutScheduler.exercise.dto.ImageUrlViewModel(
                                imageUrl.getId(),
                                imageUrl.getUrl()
                        ))
                        .toList()
        );
    }

    @Transactional
    public void editExercise(ExerciseEditBindingModel exerciseEdit) {
        Exercise exercise = exerciseRepository
                .findById(exerciseEdit.id())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        exercise.setName(exerciseEdit.name());
        exercise.setDescription(exerciseEdit.description());

        if (exerciseEdit.addImageUrls() != null && !exerciseEdit.addImageUrls().isBlank()) {
            List<ImageUrl> list = Arrays.stream(exerciseEdit.addImageUrls().split(System.lineSeparator()))
                    .map(url -> {
                        ImageUrl imageUrl = new ImageUrl();
                        imageUrl.setUrl(url.trim());
                        imageUrl.setExercise(exercise);
                        exercise.getImageURLs().add(imageUrl);
                        return imageUrl;
                    }).toList();
            exercise.getImageURLs().addAll(list);
        }

        exerciseRepository.save(exercise);
    }

    public void deleteImageUrl(long id) {
        imageUrlRepository.deleteById(id);
    }

    public ExerciseEditBindingModel getExerciseEditBindingModel(long id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        return new ExerciseEditBindingModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                null,
                exercise.getApproved()
        );
    }

    public List<ImageUrlViewModel> getExerciseImages(long exerciseId) {
        List<ImageUrlViewModel> result = imageUrlRepository.findByExercise_Id(exerciseId).stream()
                .map(imageUrl -> new ImageUrlViewModel(imageUrl.getId(), imageUrl.getUrl()))
                .toList();

        return result.isEmpty() ? new ArrayList<>() : result;

    }

    public List<ExerciseNameAndIdViewModel> getExercisesViewByTargets(String[] targetBodyParts) {
        if (targetBodyParts.length == 0 || targetBodyParts[0].equals("ALL")) {

            return exerciseRepository.findAllByApprovedTrue()
                    .stream()
                    .map(ex -> new ExerciseNameAndIdViewModel(ex.getId(), ex.getName()))
                    .toList();
        }
        List<TargetBodyPart> targetBodyPartList = Arrays.stream(targetBodyParts).map(TargetBodyPart::valueOf).toList();


        return exerciseRepository
                .findAllByApprovedTrueAndTargetBodyPartIsIn(targetBodyPartList)
                .stream()
                .map(ex -> new ExerciseNameAndIdViewModel(ex.getId(), ex.getName()))
                .toList();
    }

    public List<ExerciseNameAndIdViewModel> getExercisesForTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
        return exerciseRepository.findAllByApprovedTrueAndTargetBodyPartIsIn(targetBodyParts)
                .stream()
                .map(ex -> new ExerciseNameAndIdViewModel(ex.getId(), ex.getName()))
                .toList();
    }
}