INSERT INTO tbl_shops (name, url) VALUES ('ElectroShop', 'www.electro-shop.com');

INSERT INTO tbl_shop_users (email, password_ssha256_hex, created_at, name_first, name_last, language, is_active, shop_id) 
VALUES ('electro@electro.net', '9860c5afc4e0b5621052ea044d6c8bb1ee1f56bee662e2d0766b44bfef67ba46', '1416231736074', 'F', 'L', 'en', 'true', '4');

INSERT INTO tbl_currencies (title) VALUES('Euro');
