-- Schema initialization for H2 testing database
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS employee;

CREATE TABLE members (
  user_id VARCHAR(50) NOT NULL PRIMARY KEY,
  pw CHAR(68) NOT NULL,
  active TINYINT NOT NULL
);

-- Seed members with BCrypt hashed password for 'fun123'
INSERT INTO members (user_id, pw, active) VALUES
('john', '{bcrypt}$2a$10$qeS0HEh7urweMojsnwNAR.vcXJeXR1UcMRZ2WcGQl9YeuspUdgF.q', 1),
('mary', '{bcrypt}$2a$10$qeS0HEh7urweMojsnwNAR.vcXJeXR1UcMRZ2WcGQl9YeuspUdgF.q', 1),
('susan', '{bcrypt}$2a$10$qeS0HEh7urweMojsnwNAR.vcXJeXR1UcMRZ2WcGQl9YeuspUdgF.q', 1);

CREATE TABLE roles (
  user_id VARCHAR(50) NOT NULL,
  role VARCHAR(50) NOT NULL,
  CONSTRAINT fk_roles_members FOREIGN KEY (user_id) REFERENCES members (user_id)
);

-- Seed role hierarchies
INSERT INTO roles (user_id, role) VALUES
('john', 'ROLE_EMPLOYEE'),
('mary', 'ROLE_EMPLOYEE'),
('mary', 'ROLE_MANAGER'),
('susan', 'ROLE_EMPLOYEE'),
('susan', 'ROLE_MANAGER'),
('susan', 'ROLE_ADMIN');

CREATE TABLE employee (
  id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(45) DEFAULT NULL,
  last_name VARCHAR(45) DEFAULT NULL,
  email VARCHAR(45) DEFAULT NULL,
  department VARCHAR(45) DEFAULT NULL
);

-- Seed initial employee records
INSERT INTO employee (first_name, last_name, email, department) VALUES
('Leslie', 'Andrews', 'leslie@luv2code.com', 'Engineering'),
('Emma', 'Baumgarten', 'emma@luv2code.com', 'Human Resources'),
('Avani', 'Gupta', 'avani@luv2code.com', 'Quality Assurance'),
('Yuri', 'Petrov', 'yuri@luv2code.com', 'Cybersecurity'),
('Juan', 'Vega', 'juan@luv2code.com', 'Product Management');
