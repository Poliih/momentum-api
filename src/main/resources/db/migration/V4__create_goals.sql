CREATE TABLE goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    daily_focus_minutes INTEGER NOT NULL DEFAULT 120,
    daily_pomodoros INTEGER NOT NULL DEFAULT 4,
    daily_tasks INTEGER NOT NULL DEFAULT 3,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_goals_user_id ON goals (user_id);
