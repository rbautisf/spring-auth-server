CREATE TABLE IF NOT EXISTS auth_user (
                                  id uuid NOT NULL,
                                  username varchar(255),
                                  password varchar(255),
                                  enabled boolean,
                                  account_non_expired boolean,
                                  account_non_locked boolean,
                                  credentials_non_expired boolean
);

CREATE TABLE IF NOT EXISTS role (
                             id uuid NOT NULL,
                             type character varying(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
                                   role_id uuid NOT NULL,
                                   user_id uuid NOT NULL
);

CREATE TABLE IF NOT EXISTS oauth2_registered_client (
                                                 id varchar(100) NOT NULL,
                                                 client_id varchar(100) NOT NULL,
                                                 client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                 client_secret varchar(200) DEFAULT NULL,
                                                 client_secret_expires_at timestamp DEFAULT NULL,
                                                 client_name varchar(200) NOT NULL,
                                                 client_authentication_methods varchar(1000) NOT NULL,
                                                 authorization_grant_types varchar(1000) NOT NULL,
                                                 redirect_uris varchar(1000) DEFAULT NULL,
                                                 post_logout_redirect_uris varchar(1000) DEFAULT NULL,
                                                 scopes varchar(1000) NOT NULL,
                                                 client_settings varchar(2000) NOT NULL,
                                                 token_settings varchar(2000) NOT NULL
);

CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
                                                     registered_client_id varchar(100) NOT NULL,
                                                     principal_name varchar(200) NOT NULL,
                                                     authorities varchar(1000) NOT NULL
);

CREATE TABLE IF NOT EXISTS oauth2_authorization (
                                             id varchar(100) NOT NULL,
                                             registered_client_id varchar(100) NOT NULL,
                                             principal_name varchar(200) NOT NULL,
                                             authorization_grant_type varchar(100) NOT NULL,
                                             authorized_scopes varchar(1000) DEFAULT NULL,
                                             attributes character varying(4000) DEFAULT NULL,
                                             state varchar(500) DEFAULT NULL,
                                             authorization_code_value character varying(4000) DEFAULT NULL,
                                             authorization_code_issued_at timestamp DEFAULT NULL,
                                             authorization_code_expires_at timestamp DEFAULT NULL,
                                             authorization_code_metadata character varying(255) DEFAULT NULL,
                                             access_token_value character varying(4000) DEFAULT NULL,
                                             access_token_issued_at timestamp DEFAULT NULL,
                                             access_token_expires_at timestamp DEFAULT NULL,
                                             access_token_metadata character varying(2000) DEFAULT NULL,
                                             access_token_type varchar(100) DEFAULT NULL,
                                             access_token_scopes varchar(1000) DEFAULT NULL,
                                             oidc_id_token_value character varying(4000) DEFAULT NULL,
                                             oidc_id_token_issued_at timestamp DEFAULT NULL,
                                             oidc_id_token_expires_at timestamp DEFAULT NULL,
                                             oidc_id_token_metadata character varying(4000) DEFAULT NULL,
                                             refresh_token_value character varying(4000) DEFAULT NULL,
                                             refresh_token_issued_at timestamp DEFAULT NULL,
                                             refresh_token_expires_at timestamp DEFAULT NULL,
                                             refresh_token_metadata character varying(4000) DEFAULT NULL,
                                             user_code_value character varying(4000) DEFAULT NULL,
                                             user_code_issued_at timestamp DEFAULT NULL,
                                             user_code_expires_at timestamp DEFAULT NULL,
                                             user_code_metadata character varying(4000) DEFAULT NULL,
                                             device_code_value character varying(4000) DEFAULT NULL,
                                             device_code_issued_at timestamp DEFAULT NULL,
                                             device_code_expires_at timestamp DEFAULT NULL,
                                             device_code_metadata character varying(4000) DEFAULT NULL
);

ALTER TABLE auth_user
    ADD CONSTRAINT auth_user_pkey PRIMARY KEY (id);

ALTER TABLE auth_user
    ADD CONSTRAINT auth_user_unique_username UNIQUE (username);

ALTER TABLE role
    ADD CONSTRAINT auth_user_role_pkey PRIMARY KEY (id);

ALTER TABLE user_roles
    ADD CONSTRAINT auth_user_roles_pkey PRIMARY KEY (role_id, user_id);

ALTER TABLE oauth2_authorization_consent
    ADD CONSTRAINT oauth2_authorization_consent_pkey PRIMARY KEY (registered_client_id, principal_name);

ALTER TABLE oauth2_authorization
    ADD CONSTRAINT oauth2_authorization_pkey PRIMARY KEY (id);

ALTER TABLE oauth2_registered_client
    ADD CONSTRAINT oauth2_registered_client_pkey PRIMARY KEY (id);
