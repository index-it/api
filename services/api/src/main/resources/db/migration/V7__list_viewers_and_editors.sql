CREATE TABLE IF NOT EXISTS listviewer (id SERIAL PRIMARY KEY, id_list uuid NOT NULL, id_user uuid NOT NULL, CONSTRAINT fk_listviewer_id_list__id FOREIGN KEY (id_list) REFERENCES list(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_listviewer_id_user__id FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX listviewer_id_list ON listviewer (id_list);
CREATE INDEX listviewer_id_user ON listviewer (id_user);
CREATE TABLE IF NOT EXISTS listeditor (id SERIAL PRIMARY KEY, id_list uuid NOT NULL, id_user uuid NOT NULL, CONSTRAINT fk_listeditor_id_list__id FOREIGN KEY (id_list) REFERENCES list(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_listeditor_id_user__id FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX listeditor_id_list ON listeditor (id_list);
CREATE INDEX listeditor_id_user ON listeditor (id_user);