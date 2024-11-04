INSERT INTO users (id, username, password, email, first_name, last_name)
VALUES
(21, 'testUser', 'password123', 'testuser@example.com', 'Test', 'User'),
(5, 'testManager', '123456789', 'testmanager@example.com', 'TestManager', 'Manager'),
(2, 'testUser2', '$2a$10$6rM3udvWooUD36Nc69JfiujZFrZTsDWSabwREl6.mA4EzXBZly1xm',
'testuser2@example.com', 'Test2', 'User2'); /* password = 12345678 */