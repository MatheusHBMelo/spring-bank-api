-- Permissões básicas
INSERT INTO tb_permissions (name)
VALUES
    ('READ'),
    ('WRITE'),
    ('UPDATE'),
    ('DELETE')
ON CONFLICT (name) DO NOTHING;

-- Roles básicas
INSERT INTO tb_roles (name)
VALUES
    ('USER'),
    ('ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Permissão de leitura para ROLE USER
INSERT INTO tb_role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM
    (SELECT id FROM tb_roles WHERE name = 'USER') r,
    (SELECT id FROM tb_permissions WHERE name = 'READ') p
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Todas permissões para ROLE ADMIN
INSERT INTO tb_role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM
    (SELECT id FROM tb_roles WHERE name = 'ADMIN') r,
    tb_permissions p
ON CONFLICT (role_id, permission_id) DO NOTHING;