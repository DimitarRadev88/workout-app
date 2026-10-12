package com.dimitarrradev.workoutScheduler.exercise.service;

import com.dimitarrradev.workoutScheduler.config.rest.ExerciseRestClient;
import com.dimitarrradev.workoutScheduler.errors.exception.ExerciseNotFoundException;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseFindBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.ExerciseFindViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.ExerciseNameAndIdViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.ExerciseViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.ImageUrlViewModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceUnitTests {

    @Mock
    private ExerciseRestClient restClient;
    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void testFindActiveExercisesPageConvertsOneBasedPageNumber() {
        ExerciseFindBindingModel exerciseFind = new ExerciseFindBindingModel("push", TargetBodyPart.CHEST, null, MovementType.COMPOUND);
        Page<ExerciseFindViewModel> page = new ExercisePageImpl<>(List.of());

        when(restClient.searchExercises("push", TargetBodyPart.CHEST, null, MovementType.COMPOUND, 1, 10, "asc"))
                .thenReturn(page);

        assertSame(page, exerciseService.findActiveExercisesPage(exerciseFind, 2, 10, "asc"));
    }

    @Test
    void testGetExercisesForTargetBodyPartsReturnsNamesAndIds() {
        when(restClient.getExercises(List.of(TargetBodyPart.CHEST)))
                .thenReturn(List.of(exerciseResponse(3L, "push-up")));

        assertEquals(
                List.of(new ExerciseNameAndIdViewModel(3L, "push-up")),
                exerciseService.getExercisesForTargetBodyParts(List.of(TargetBodyPart.CHEST))
        );
    }

    @Test
    void testGetAllActiveExercisesRequestsEveryApprovedExercise() {
        when(restClient.getExercises(List.of()))
                .thenReturn(List.of(exerciseResponse(3L, "push-up")));

        assertEquals(
                List.of(new ExerciseFindViewModel(3L, "push-up", Complexity.EASY, MovementType.COMPOUND)),
                exerciseService.getAllActiveExercises()
        );
    }

    @Test
    void testGetExerciseNamesLooksUpEachExerciseOnceAndFallsBackWhenMissing() {
        when(restClient.getExercise(1L))
                .thenReturn(ResponseEntity.ok(new ExerciseViewModel(1L, "push-up", null, null, null, null, true, null)));
        when(restClient.getExercise(2L))
                .thenThrow(new ExerciseNotFoundException("Exercise with id 2 not found"));

        Map<Long, String> names = exerciseService.getExerciseNames(List.of(1L, 2L, 1L));

        assertEquals(Map.of(1L, "push-up", 2L, "Unknown exercise"), names);
        verify(restClient, times(1)).getExercise(1L);
    }

    @Test
    void testGetExerciseViewIncludesImages() {
        when(restClient.getExercise(1L))
                .thenReturn(ResponseEntity.ok(new ExerciseViewModel(1L, "push-up", "EASY", "COMPOUND", "description", "user", true, null)));
        when(restClient.getExerciseImages(1L))
                .thenReturn(List.of(new ImageUrlViewModel(11L, "https://images.example/push-up.png")));

        assertEquals(
                new ExerciseViewModel(1L, "push-up", "EASY", "COMPOUND", "description", "user", true,
                        List.of(new ImageUrlViewModel(11L, "https://images.example/push-up.png"))),
                exerciseService.getExerciseView(1L)
        );
    }

    @Test
    void testDeleteImageUrlDeletesImageOfExercise() {
        exerciseService.deleteImageUrl(3L, 11L);

        verify(restClient, times(1)).deleteExerciseImage(3L, 11L);
    }

    private static ExerciseRestClient.ExerciseResponse exerciseResponse(Long id, String name) {
        return new ExerciseRestClient.ExerciseResponse(id, name, "description", TargetBodyPart.CHEST, Complexity.EASY, MovementType.COMPOUND, true, "user");
    }

}
