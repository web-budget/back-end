INSERT INTO administration.users (id, external_id, version, created_on, name, email, password, active, default_language)
VALUES (100, '6706a395-6690-4bad-948a-5c3c823e93d2', 0, current_timestamp, 'User', 'user@webbudget.com.br',
        '$2a$11$gofMXY5BBXVbwoPHbYiCd.6PJ.2pIZk01XRiI8gnVut8t6kFWoOle', true, 'PT_BR');

INSERT INTO administration.authorities(id, external_id, version, created_on, name)
VALUES (100, '0d5f0878-0926-4735-a4f6-83d3d270db74', 0, current_timestamp, 'DASHBOARDS');
INSERT INTO administration.authorities(id, external_id, version, created_on, name)
VALUES (101, '686f3fa3-6a08-4a5a-8587-d27b94a64097', 0, current_timestamp, 'OTHER');

INSERT INTO administration.grants (external_id, version, created_on, id_user, id_authority)
VALUES ('05e9d984-8bff-4842-9feb-fd2c513a08e8', 0, current_timestamp, 100, 100);