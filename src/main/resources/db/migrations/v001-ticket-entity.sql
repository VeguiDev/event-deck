CREATE TABLE IF NOT EXISTS tickets (
    id TEXT PRIMARY KEY,
    code TEXT NOT NULL,

    attendeeName TEXT NOT NULL,
    attendeeEmail TEXT NOT NULL,

    event_id TEXT,

    created_at TEXT NOT NULL,
    attended_at TEXT,
    deleted_at TEXT,

    FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

