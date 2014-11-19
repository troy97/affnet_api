BEGIN;


-- Subtitute NULLs with zeros ---------------
UPDATE tbl_clicks SET click_time = 0 WHERE click_time IS NULL;
-- -------------------------------------------------------------;

COMMIT;
