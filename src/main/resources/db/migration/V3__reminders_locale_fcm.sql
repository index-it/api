CREATE TABLE IF NOT EXISTS taskreminder (id SERIAL PRIMARY KEY, id_task uuid NOT NULL, days_before INT NOT NULL, time_offset BIGINT NOT NULL);
CREATE INDEX taskreminder_id_task ON taskreminder (id_task);
CREATE TABLE IF NOT EXISTS taskreminderjob (id uuid PRIMARY KEY, id_task uuid NOT NULL, id_user uuid NOT NULL, scheduled_at BIGINT NOT NULL);
CREATE INDEX taskreminderjob_id_task ON taskreminderjob (id_task);
CREATE INDEX taskreminderjob_id_user ON taskreminderjob (id_user);

ALTER TABLE namesuggestion
    ADD language_locale CHAR(2) NOT NULL
    DEFAULT ('en');

ALTER TABLE namesuggestion
    DROP CONSTRAINT namesuggestion_pkey;

ALTER TABLE namesuggestion
    ADD CONSTRAINT pk_NameSuggestion PRIMARY KEY (id, language_locale);

CREATE INDEX namesuggestion_id ON namesuggestion (id);
CREATE INDEX namesuggestion_language_locale ON namesuggestion (language_locale);

CREATE TABLE IF NOT EXISTS fcmregistrationtoken (id SERIAL PRIMARY KEY, token VARCHAR(100) NOT NULL, id_user uuid NOT NULL, created_at BIGINT NOT NULL, CONSTRAINT fk_fcmregistrationtoken_id_user__id FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT);
ALTER TABLE fcmregistrationtoken ADD CONSTRAINT fcmregistrationtoken_token_unique UNIQUE (token);
CREATE INDEX fcmregistrationtoken_id_user ON fcmregistrationtoken (id_user);