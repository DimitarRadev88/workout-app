package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.ImageUrl;
import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseRepository;
import com.dimitarrradev.workoutScheduler.exercise.dao.ImageUrlRepository;
import com.dimitarrradev.workoutScheduler.exercise.dto.*;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.util.mapping.WorkoutSchedulerMapper;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseFindBindingModel;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private static final Logger log = LoggerFactory.getLogger(ExerciseService.class);
    private final ExerciseRepository exerciseRepository;
    private final ImageUrlRepository imageUrlRepository;
    @Qualifier("exerciseMapper")
    private final WorkoutSchedulerMapper<Exercise> mapper;

    public ExerciseService(ExerciseRepository exerciseRepository, ImageUrlRepository imageUrlRepository, WorkoutSchedulerMapper<Exercise> workoutSchedulerMapper) {
        this.exerciseRepository = exerciseRepository;
        this.imageUrlRepository = imageUrlRepository;
        this.mapper = workoutSchedulerMapper;
    }

    public void addExerciseForReview(ExerciseAddBindingModel exerciseAdd) {
        if (exerciseRepository.existsExerciseByName(exerciseAdd.exerciseName())) {
            throw new IllegalArgumentException("Exercise already exists");
        }

        Exercise exercise = mapper.mapFrom(exerciseAdd);

        exerciseRepository.save(exercise);
    }

    public long getExercisesForReviewCount() {
        return exerciseRepository.countAllByApprovedFalse();
    }

    public Page<ExerciseForReviewViewModel> getExercisesForReviewPage(int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return exerciseRepository
                .findAllByApprovedIsAndNameContainingIgnoreCase(pageable, false, "")
                .map(exercise -> new ExerciseForReviewViewModel(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getDescription(),
                        exercise.getComplexity(),
                        exercise.getMovementType(),
                        exercise.getAddedBy()
                ));
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

    public Page<ExerciseFindViewModel> findActiveExercisesPage(ExerciseFindBindingModel exerciseFind, int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        if (exerciseFind.name() != null && !exerciseFind.name().isEmpty()) {
            return getActiveExercisesWithNameContaining(exerciseFind.name(), pageable);
        }

        TargetBodyPart targetBodyPart = exerciseFind.targetBodyPart() == null ? TargetBodyPart.ALL : exerciseFind.targetBodyPart();
        Complexity complexity = exerciseFind.complexity() == null ? Complexity.ALL : exerciseFind.complexity();
        MovementType movementType = exerciseFind.movementType() == null ? MovementType.ALL : exerciseFind.movementType();

        if (!targetBodyPart.equals(TargetBodyPart.ALL) && !complexity.equals(Complexity.ALL) && !movementType.equals(MovementType.ALL)) {
            return getExerciseFindViewModelPageByTargetBodyPartComplexityMovementTypeAndActiveTrue(pageable, targetBodyPart, complexity, movementType);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !complexity.equals(Complexity.ALL)) {
            return getExerciseFindViewModelPageByTargetBodyPartComplexityAndActiveTrue(pageable, targetBodyPart, complexity);
        } else if (!targetBodyPart.equals(TargetBodyPart.ALL) && !movementType.equals(MovementType.ALL)) {
            return getExerciseFindViewModelPageByTargetBodyPartMovementTypeAndActiveTrue(pageable, targetBodyPart, movementType);
        } else if (!complexity.equals(Complexity.ALL) && !movementType.equals(MovementType.ALL)) {
            return getExerciseFindViewModelPageByComplexityMovementTypeAndActiveTrue(pageable, complexity, movementType);
        } else if (!complexity.equals(Complexity.ALL)) {
            return getExerciseFindViewModelPageByComplexityAndActiveTrue(pageable, complexity);
        } else if (!movementType.equals(MovementType.ALL)) {
            return getExerciseFindViewModelPageByMovementTypeAndActiveTrue(pageable, movementType);
        }

        return getExerciseFindViewModelPageByActiveTrue(pageable);
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByActiveTrue(Pageable pageable) {
        return exerciseRepository
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

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByMovementTypeAndActiveTrue(Pageable pageable, MovementType movementType) {
        return exerciseRepository
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
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByComplexityAndActiveTrue(Pageable pageable, Complexity complexity) {
        return exerciseRepository
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
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByComplexityMovementTypeAndActiveTrue(Pageable pageable, Complexity complexity, MovementType movementType) {
        return exerciseRepository
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
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartMovementTypeAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, MovementType movementType) {
        return exerciseRepository
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
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartComplexityAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity) {
        return exerciseRepository
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
    }

    private Page<ExerciseFindViewModel> getExerciseFindViewModelPageByTargetBodyPartComplexityMovementTypeAndActiveTrue(Pageable pageable, TargetBodyPart targetBodyPart, Complexity complexity, MovementType movementType) {
        return exerciseRepository
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
    }


    private Page<ExerciseFindViewModel> getActiveExercisesWithNameContaining(String exerciseName, Pageable pageable) {
        return exerciseRepository
                .findAllByApprovedTrueAndNameContainingIgnoreCase(pageable, exerciseName)
                .map(exercise -> new ExerciseFindViewModel(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getComplexity(),
                        exercise.getMovementType()
                ));
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
                        .map(imageUrl -> new ImageUrlViewModel(
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
                        return imageUrl;
                    }).toList();
            exercise.getImageURLs().addAll(list);
        }

        exercise.setApproved(exerciseEdit.isApproved());

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

    public List<ExerciseNameAndIdViewModel> getExercisesForTargetBodyParts(List<TargetBodyPart> targetBodyParts) {
        return exerciseRepository.findAllByApprovedTrueAndTargetBodyPartIsIn(targetBodyParts)
                .stream()
                .map(ex -> new ExerciseNameAndIdViewModel(ex.getId(), ex.getName()))
                .toList();
    }

    public Exercise getExercise(Long id) {
        return exerciseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    public <T> PageInformation getPageInfo(Page<T> page) {
        long elementsShownFrom = page.getTotalElements() == 0 ? 0 : Math.min(page.getTotalElements(), (long) (page.getNumber() + 1) * page.getSize());
        long elementsShownTo = (page.getNumber() + 1) < page.getTotalPages() ? (long) (page.getNumber() + 1) * page.getSize() : page.getTotalElements();
        List<Integer> pageSizes = List.of(5, 10, 25, 50);

        return new PageInformation(
                String.format("Showing %d to %d of %d exercises",
                        elementsShownFrom,
                        elementsShownTo,
                        page.getTotalElements()
                ),
                pageSizes
        );
    }
}