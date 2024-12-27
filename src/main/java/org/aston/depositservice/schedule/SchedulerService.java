package org.aston.depositservice.schedule;

import lombok.RequiredArgsConstructor;
import org.aston.depositservice.service.impl.SchedulerTasks;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerService {

    private final SchedulerTasks schedulerTasks;

    @Scheduled(cron = "${crone-for-simple-calculating}")
    public void runSimpleCalculatingTask() {
        schedulerTasks.calculateSimplePercent();
    }
}
