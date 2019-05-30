package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;


    public TimeEntryController(TimeEntryRepository ter) {
        this.timeEntryRepository = ter;
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry timeEntryCreated = timeEntryRepository.create(timeEntry);
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
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(foundEntry, status);
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        return new ResponseEntity<>(this.timeEntryRepository.list(), HttpStatus.OK);
    }

    @PutMapping("/{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry expected) {
        TimeEntry updated = this.timeEntryRepository.update(timeEntryId, expected);
        HttpStatus status = HttpStatus.OK;
        if (updated == null) {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(updated, status);
    }

    @DeleteMapping("/{timeEntryId}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long timeEntryId) {
        TimeEntry found = this.timeEntryRepository.delete(timeEntryId);
        return new ResponseEntity<>(found, HttpStatus.NO_CONTENT);
    }
}
