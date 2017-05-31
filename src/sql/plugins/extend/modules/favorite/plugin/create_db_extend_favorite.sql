--
-- Structure for table extend_favorite
--
DROP TABLE IF EXISTS extend_favorite;
CREATE TABLE extend_favorite (
	id_favorite INT DEFAULT 0 NOT NULL,
	id_resource VARCHAR(100) DEFAULT '' NOT NULL,
	resource_type VARCHAR(255) DEFAULT '' NOT NULL,
	favorite_count INT default 0 NOT NULL,
	PRIMARY KEY (id_favorite)
);

--
-- Structure for table extend_favorite_history
--
DROP TABLE IF EXISTS extend_favorite_history;
CREATE TABLE extend_favorite_history (
	id_favorite_history INT DEFAULT 0 NOT NULL,
	id_extender_history INT DEFAULT 0 NOT NULL,
	favorite_value INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_favorite_history)
);