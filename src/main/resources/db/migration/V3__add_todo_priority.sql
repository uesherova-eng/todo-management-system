
ALTER TABLE todos
    ADD COLUMN priority VARCHAR(20)
        NOT NULL DEFAULT 'Medium';
