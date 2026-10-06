CREATE TABLE focus_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    task_id UUID REFERENCES tasks(id) ON DELETE SET NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    planned_duration_seconds INTEGER NOT NULL,
    started_at TIMESTAMP NOT NULL,
    paused_at TIMESTAMP,
    accumulated_pause_seconds INTEGER NOT NULL DEFAULT 0,
    ended_at TIMESTAMP,
    actual_duration_seconds INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX ix_focus_sessions_user_id ON focus_sessions (user_id);
CREATE INDEX ix_focus_sessions_user_started ON focus_sessions (user_id, started_at);
CREATE INDEX ix_focus_sessions_task_id ON focus_sessions (task_id);
