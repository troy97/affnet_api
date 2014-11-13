BEGIN;


-- DROP FIELD "auth_login" -------------------------------------
ALTER TABLE "public"."tbl_shops" DROP COLUMN "auth_login";
-- -------------------------------------------------------------

-- DROP FIELD "auth_password" ----------------------------------
ALTER TABLE "public"."tbl_shops" DROP COLUMN "auth_password";
-- -------------------------------------------------------------

-- DROP FIELD "price_list_url" ---------------------------------
ALTER TABLE "public"."tbl_shops" DROP COLUMN "price_list_url";
-- -------------------------------------------------------------;

COMMIT;