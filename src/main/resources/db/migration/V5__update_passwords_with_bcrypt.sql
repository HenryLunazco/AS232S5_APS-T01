-- Update existing passwords to BCrypt hashed versions
-- BCrypt hash for "12345678" (strength 10) for admin user
UPDATE users 
SET password_hash = '$2a$10$VO.loowvpqN28Pu1RRLU5eRbsn7oNjWx44/.7kVM8vq1LJNY16cPK'
WHERE email = 'admin@colegio.edu' AND password_hash NOT LIKE '$2a$%';
