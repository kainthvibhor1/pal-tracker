package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private Map<Long, TimeEntry> dB;
    private long currentMaxId;

    public InMemoryTimeEntryRepository() {
        this.dB = new HashMap<>();
        this.currentMaxId = 1L;
    }

    @Override
    public TimeEntry create(TimeEntry any) {

        this.dB.put(currentMaxId, any);
        any.setId(currentMaxId);
        currentMaxId++;
        return any;

    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return dB.get(timeEntryId);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(dB.values());
    }

    @Override
    public TimeEntry update(long eq, TimeEntry any) {
        TimeEntry foundEntry = this.find(eq);
        if (foundEntry == null) {
            return null;
        }
        foundEntry.setId(eq);
        foundEntry.setDate(any.getDate());
        foundEntry.setHours(any.getHours());
        foundEntry.setProjectId(any.getProjectId());
        foundEntry.setUserId(any.getUserId());
        return foundEntry;
    }

    @Override
    public TimeEntry delete(long timeEntryId) {
        TimeEntry found = this.dB.get(timeEntryId);
        this.dB.remove(timeEntryId);
        return found;
    }
}
