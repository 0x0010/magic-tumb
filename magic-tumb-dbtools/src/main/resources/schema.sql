CREATE SCHEMA IF NOT EXISTS magic_tumblr;
DROP TABLE IF EXISTS magic_tumblr.magic_asset;
CREATE TABLE magic_tumblr.`magic_asset` ( `id` int(11) unsigned NOT NULL AUTO_INCREMENT, `asset_id` varchar(32) DEFAULT NULL, `video_code` varchar(32) DEFAULT NULL, `image_code` varchar(32) DEFAULT NULL, `url_hash` varchar(32) DEFAULT NULL,`original_url` varchar(256) DEFAULT NULL, `create_time` datetime DEFAULT NULL, `state` int(1) DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE INDEX idx_asset_id on magic_tumblr.`magic_asset`(asset_id);
CREATE INDEX idx_url_hash on magic_tumblr.`magic_asset`(url_hash);