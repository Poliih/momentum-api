package com.momentum.api.controller;

import com.momentum.api.dto.statistics.StatisticsResponse;
import com.momentum.api.dto.tag.TagWeeklyStatResponse;
import com.momentum.api.security.CurrentUser;
import com.momentum.api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public StatisticsResponse get() {
        return statisticsService.getStatistics(CurrentUser.id());
    }

    @GetMapping("/tags/weekly")
    public List<TagWeeklyStatResponse> weeklyByTag() {
        return statisticsService.getWeeklyTagBreakdown(CurrentUser.id());
    }
}
