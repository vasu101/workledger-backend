package com.workledger.core.timesheet.controller;


import com.workledger.core.timesheet.service.WorkEntryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/work-entries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Work Entries", description = "Work entry management APIs")
public class WorkEntryController {

    private final WorkEntryService workEntryService;
}
