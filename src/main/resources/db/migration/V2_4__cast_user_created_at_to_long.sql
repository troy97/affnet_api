BEGIN;


-- CHANGE "TYPE" OF "FIELD "created_at" ------------------------
ALTER TABLE "public"."tbl_shop_users" ALTER COLUMN "created_at" TYPE bigint USING EXTRACT(EPOCH FROM "created_at");
-- -------------------------------------------------------------

-- CHANGE "DEFAULT VALUE" OF "FIELD "created_at" ---------------
ALTER TABLE "public"."tbl_shop_users" ALTER COLUMN "created_at" SET DEFAULT '0';
-- -------------------------------------------------------------;

COMMIT;
