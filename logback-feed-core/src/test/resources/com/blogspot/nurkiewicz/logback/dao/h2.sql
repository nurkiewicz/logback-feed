DROP TABLE IF EXISTS    logging_event_property;
DROP TABLE IF EXISTS    logging_event_exception;
DROP SEQUENCE IF EXISTS logging_event_id_seq;
DROP TABLE IF EXISTS    logging_event;


CREATE SEQUENCE logging_event_id_seq;


CREATE TABLE logging_event 
  (
    timestmp          BIGINT NOT NULL,
   	formatted_message VARCHAR(4000) NOT NULL,
    logger_name       VARCHAR(254) NOT NULL,
    level_string      VARCHAR(254) NOT NULL,
    thread_name       VARCHAR(254),
    reference_flag    SMALLINT,
    caller_filename   VARCHAR(254) NOT NULL,
    caller_class      VARCHAR(254) NOT NULL,
    caller_method     VARCHAR(254) NOT NULL,
    caller_line       CHAR(4) NOT NULL,
    event_id          INT DEFAULT nextval('logging_event_id_seq') PRIMARY KEY
  );

CREATE TABLE logging_event_property
  (
    event_id	      INT NOT NULL,
    mapped_key        VARCHAR(254) NOT NULL,
    mapped_value      VARCHAR(1024),
    PRIMARY KEY(event_id, mapped_key),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
  );

CREATE TABLE logging_event_exception
  (
    event_id          INT NOT NULL,
    i                 SMALLINT NOT NULL,
    trace_line        VARCHAR(254) NOT NULL,
    PRIMARY KEY(event_id, i),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
  );
