BEGIN;


-- CHANGE "NULLABLE" OF "FIELD "file_size" ---------------------
ALTER TABLE "public"."tbl_file_templates" ALTER COLUMN "file_size" SET NOT NULL;
-- -------------------------------------------------------------

-- CHANGE "NULLABLE" OF "FIELD "is_active" ---------------------
ALTER TABLE "public"."tbl_file_templates" ALTER COLUMN "is_active" SET NOT NULL;
-- -------------------------------------------------------------

-- CHANGE "TYPE" OF "FIELD "products_count" --------------------
ALTER TABLE "public"."tbl_file_templates" ALTER COLUMN "products_count" TYPE Integer;
-- -------------------------------------------------------------

-- CHANGE "NULLABLE" OF "FIELD "products_count" ----------------
ALTER TABLE "public"."tbl_file_templates" ALTER COLUMN "products_count" SET NOT NULL;
-- -------------------------------------------------------------;

COMMIT;