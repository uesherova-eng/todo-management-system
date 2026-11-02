ALTER TABLE todos
    ADD COLUMN status VARCHAR(20);

UPDATE todos
SET status = CASE
                 WHEN completed = TRUE THEN 'Completed'
                 ELSE 'To Do'
    END;

ALTER TABLE todos
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE todos
    ALTER COLUMN status SET DEFAULT 'To Do';