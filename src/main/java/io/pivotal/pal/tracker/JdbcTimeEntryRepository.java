package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );
    private final ResultSetExtractor<TimeEntry> extractor = (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
    private JdbcTemplate template;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder holder = new GeneratedKeyHolder();

        this.template.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES  (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );
            stmt.setLong(1, timeEntry.getProjectId());
            stmt.setLong(2, timeEntry.getUserId());
            stmt.setDate(3, Date.valueOf(timeEntry.getDate()));
            stmt.setInt(4, timeEntry.getHours());

            return stmt;
        }, holder);
        return this.find(holder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return this.template.query("SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{timeEntryId}, extractor);
    }

    @Override
    public List<TimeEntry> list() {
        return this.template.query("SELECT id, project_id, user_id, date, hours FROM time_entries", mapper);
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry timeEntry) {
        this.template.update("UPDATE time_entries " +
                        "SET project_id = ?, user_id = ?, date = ?, hours = ? " +
                        "WHERE id = ?",
                timeEntry.getProjectId(), timeEntry.getUserId(), Date.valueOf(timeEntry.getDate()), timeEntry.getHours(), timeEntryId);
        return find(timeEntryId);
    }

    @Override
    public TimeEntry delete(long timeEntryId) {
        TimeEntry foundEntry = find(timeEntryId);
        this.template.update("DELETE FROM time_entries WHERE id = ?", timeEntryId);
        return foundEntry;
    }
}
