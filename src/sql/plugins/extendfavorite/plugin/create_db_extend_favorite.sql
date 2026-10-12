-- liquibase formatted sql
-- changeset extendfavorite:create_db_extend_favorite.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM extend_favorite WHERE 1 = 0
--
-- Structure for table extend_favorite
--
DROP TABLE IF EXISTS extend_favorite;
CREATE TABLE extend_favorite (
	id_favorite INT AUTO_INCREMENT,
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
	id_favorite_history INT AUTO_INCREMENT,
	id_extender_history INT DEFAULT 0 NOT NULL,
	favorite_value INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_favorite_history)
);

-- changeset extendfavorite:create_db_extend_favorite-rev1.sql dbms:mariadb,mysql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'extend_favorite' AND column_name = 'id_favorite' AND extra NOT LIKE '%auto_increment%'
SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';
ALTER TABLE extend_favorite MODIFY COLUMN id_favorite INT AUTO_INCREMENT;

-- changeset extendfavorite:create_db_extend_favorite-rev2.sql dbms:mariadb,mysql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'extend_favorite_history' AND column_name = 'id_favorite_history' AND extra NOT LIKE '%auto_increment%'
SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';
ALTER TABLE extend_favorite_history MODIFY COLUMN id_favorite_history INT AUTO_INCREMENT;
