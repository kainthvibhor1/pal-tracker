package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;
    private Counter timeEntryCounter;
    private DistributionSummary timeEntrySummary;


    public TimeEntryController(TimeEntryRepository ter, MeterRegistry registry) {
        this.timeEntryRepository = ter;
        timeEntrySummary = registry.summary("timeEntry.summary");
        timeEntryCounter = registry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry timeEntryCreated = timeEntryRepository.create(timeEntry);
        timeEntryCounter.increment();
        timeEntrySummary.record(this.timeEntryRepository.list().size());
        return new ResponseEntity<>(timeEntryCreated, HttpStatus.CREATED);
    }

    @GetMapping("/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable long timeEntryId) {
        // Find the current id from the repo
        TimeEntry foundEntry = this.timeEntryRepository.find(timeEntryId);
        HttpStatus status;
        if (foundEntry == null) {
            status = HttpStatus.NOT_FOUND;
        } else {
            timeEntryCounter.increment();
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(foundEntry, status);
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        timeEntryCounter.increment();
        return new ResponseEntity<>(this.timeEntryRepository.list(), HttpStatus.OK);
    }

    @PutMapping("/{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry expected) {
        TimeEntry updated = this.timeEntryRepository.update(timeEntryId, expected);
        HttpStatus status = HttpStatus.OK;
        if (updated == null) {
            status = HttpStatus.NOT_FOUND;
        }
        timeEntryCounter.increment();
        return new ResponseEntity<>(updated, status);
    }

    @DeleteMapping("/{timeEntryId}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long timeEntryId) {
        TimeEntry found = this.timeEntryRepository.delete(timeEntryId);
        timeEntryCounter.increment();
        timeEntrySummary.record(this.timeEntryRepository.list().size());
        return new ResponseEntity<>(found, HttpStatus.NO_CONTENT);
    }
}
