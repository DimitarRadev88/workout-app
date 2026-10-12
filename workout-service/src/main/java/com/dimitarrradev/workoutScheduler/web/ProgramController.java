package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.program.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/programs")
@RequiredArgsConstructor
public class ProgramController {
    private final ProgramService programService;

}
