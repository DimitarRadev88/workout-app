package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.program.service.ProgramService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/programs")
public class ProgramController {
    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

}
