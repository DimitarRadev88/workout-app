package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.exercise.ImageUrl;
import com.dimitarrradev.workoutScheduler.exercise.dao.ExerciseDao;
import com.dimitarrradev.workoutScheduler.exercise.dao.ImageUrlDao;
import com.dimitarrradev.workoutScheduler.exercise.dto.*;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseFindBindingModel;
import com.dimitarrradev.workoutScheduler.web.binding.ImageUrlBindingModel;
import jakarta.transaction.Transactional;
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

    private final ExerciseDao exerciseDao;
    private final ImageUrlDao imageUrlDao;

    public ExerciseService(ExerciseDao exerciseDao, ImageUrlDao imageUrlDao) {
        this.exerciseDao = exerciseDao;
        this.imageUrlDao = imageUrlDao;
    }

    public void addExerciseForReview(ExerciseAddBindingModel exerciseAdd) {
        if (exerciseDao.existsExerciseByName(exerciseAdd.exerciseName())) {
            throw new IllegalArgumentException("Exercise already exists");
        }

        Exercise exercise = new Exercise();
        exercise.setName(exerciseAdd.exerciseName());
        exercise.setDescription(exerciseAdd.description());
        exercise.setTargetBodyPart(exerciseAdd.bodyPart());
        exercise.setApproved(false);
        exercise.setAddedBy(exerciseAdd.addedBy());
        exercise.setComplexity(exerciseAdd.complexity());

        exerciseDao.save(exercise);
    }

    public long getExercisesForReviewCount() {
        return exerciseDao.countAllByApprovedFalse();
    }

    public PageAndExerciseReviewServiceView getPaginatedAndSortedDataAndExerciseActiveFalse(int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<ExerciseForReviewViewModel> page = exerciseDao
                .findAllByApprovedIs(pageable, false)
                .map(exercise -> new ExerciseForReviewViewModel(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getDescription(),
                        exercise.getEquipment(),
                        exercise.getComplexity(),
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
        Optional<Exercise> exerciseOptional = exerciseDao.findById(id);

        if (exerciseOptional.isEmpty()) {
            throw new IllegalArgumentException("Exercise not found");
        }

        exerciseOptional.ifPresent(exercise -> {
            exercise.setApproved(true);
            exerciseDao.save(exercise);
        });
    }

    public void deleteExercise(Long id) {
        Optional<Exercise> exerciseOptional = exerciseDao.findById(id);

        if (exerciseOptional.isEmpty()) {
            throw new IllegalArgumentException("Exercise not found");
        }

        exerciseOptional.ifPresent(exerciseDao::delete);
    }

    public PageAndExerciseFindServiceView getPaginatedAndSortedDataAndExerciseActiveTrue(ExerciseFindBindingModel exerciseFind, int pageNumber, int pageSize, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by("name").ascending() :
                Sort.by("name").descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<ExerciseFindViewModel> page = null;

        if (exerciseFind.targetBodyPart() != null && !exerciseFind.targetBodyPart().equals(TargetBodyPart.ALL)) {
            if (exerciseFind.complexity() != null && !exerciseFind.complexity().equals(Complexity.ALL)) {
                page = exerciseDao
                        .findAllByApprovedTrueAndTargetBodyPartAndComplexity(pageable, exerciseFind.targetBodyPart(), exerciseFind.complexity())
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity()
                        ));
            } else {
                page = exerciseDao
                        .findAllByApprovedTrueAndTargetBodyPart(pageable, exerciseFind.targetBodyPart())
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity()
                        ));
            }
        } else {
            if (exerciseFind.complexity() != null && !exerciseFind.complexity().equals(Complexity.ALL)) {
                page = exerciseDao
                        .findAllByApprovedTrueAndComplexity(pageable, exerciseFind.complexity())
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity()
                        ));
            } else {
                page = exerciseDao
                        .findAllByApprovedIs(pageable, true)
                        .map(exercise -> new ExerciseFindViewModel(
                                exercise.getId(),
                                exercise.getName(),
                                exercise.getComplexity()
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
        Exercise exercise = exerciseDao
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        return new ExerciseViewModel(
                exercise.getName(),
                exercise.getComplexity().getName(),
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
        Exercise exercise = exerciseDao
                .findById(exerciseEdit.id())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        exercise.setName(exerciseEdit.name());
        exercise.setDescription(exerciseEdit.description());
        exercise.setImageURLs(
                exerciseEdit.imageUrls() == null
                        ? new ArrayList<>()
                        : exerciseEdit.imageUrls()
                        .stream()
                        .map(image -> {
                            ImageUrl imageUrl = new ImageUrl();
                            imageUrl.setId(image.id());
                            imageUrl.setUrl(image.url());
                            return imageUrl;
                        }).toList()
        );

        if (exerciseEdit.imageUrl() != null) {
            List<ImageUrl> list = Arrays.stream(exerciseEdit.imageUrl().split(System.lineSeparator()))
                    .map(url -> {
                        ImageUrl imageUrl = new ImageUrl();
                        imageUrl.setUrl(url.trim());
                        imageUrl.setExercise(exercise);
                        exercise.getImageURLs().add(imageUrl);
                        return imageUrl;
                    }).toList();
            exercise.getImageURLs().addAll(list);
        }

        exerciseDao.save(exercise);
    }

    public void deleteImageUrl(long id) {
        imageUrlDao.deleteById(id);
    }

    public ExerciseEditBindingModel getExerciseEditBindingModel(long id) {
        Exercise exercise = exerciseDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        return new ExerciseEditBindingModel(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                null,
                exercise.getImageURLs().stream().map(imageUrl -> {
                    return new ImageUrlBindingModel(
                            imageUrl.getId(),
                            imageUrl.getUrl()
                    );
                }).toList()
        );
    }
}