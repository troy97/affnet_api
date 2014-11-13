BEGIN;


-- DROP FIELD "file_id" ----------------------------------------
ALTER TABLE "public"."tbl_file_templates" DROP COLUMN "file_id";
-- -------------------------------------------------------------;

COMMIT;