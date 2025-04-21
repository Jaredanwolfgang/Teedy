-- DBUPDATE-032-0.SQL

-- Create the table for user registration requests
create table T_USER_REGISTRATION_REQUEST (
  URR_ID_C varchar(36) not null,
  URR_USERNAME_C varchar(50) not null,
  URR_PASSWORD_C varchar(100) not null,
  URR_EMAIL_C varchar(100) not null,
  URR_CREATEDATE_D timestamp not null,
  URR_STATUS_C varchar(10) not null,
  URR_HANDLEDBY_C varchar(36),
  URR_HANDLINGDATE_D timestamp,
  primary key (URR_ID_C)
);

-- Update the database version
update T_CONFIG set CFG_VALUE_C = '32' where CFG_ID_C = 'DB_VERSION'; 