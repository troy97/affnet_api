BEGIN;


-- CHANGE "DEFAULT VALUE" OF "FIELD "click_time" ---------------
ALTER TABLE "public"."tbl_clicks" ALTER COLUMN "click_time" SET DEFAULT '0';
-- -------------------------------------------------------------;

COMMIT;