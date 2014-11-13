-- CREATE FIELD "is_processed" ---------------------------------
ALTER TABLE "public"."tbl_files" ADD COLUMN "is_processed" Boolean DEFAULT 'false' NOT NULL;
-- -------------------------------------------------------------;
