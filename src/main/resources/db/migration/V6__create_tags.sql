CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(60) NOT NULL,
    color VARCHAR(20) NOT NULL DEFAULT '#6c5ce7',
    kind VARCHAR(20) NOT NULL DEFAULT 'NEUTRAL',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX ix_tags_user_id ON tags (user_id);
CREATE UNIQUE INDEX ux_tags_user_name ON tags (user_id, name);

ALTER TABLE focus_sessions ADD COLUMN tag_id UUID REFERENCES tags(id) ON DELETE SET NULL;
CREATE INDEX ix_focus_sessions_tag_id ON focus_sessions (tag_id);
