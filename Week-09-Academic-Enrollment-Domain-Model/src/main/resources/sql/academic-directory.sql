-- ==============================================================================
-- ACADEMIC ENROLLMENT DIRECTORY SCHEMA & SEED DATA
-- ==============================================================================

DROP SCHEMA IF EXISTS `academic_directory`;
CREATE SCHEMA `academic_directory`;
USE `academic_directory`;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Instructor Detail Table (1:1 with Instructor)
CREATE TABLE `instructor_detail` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `portfolio_url` VARCHAR(256) DEFAULT NULL,
  `specialization` VARCHAR(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 2. Instructor Table (1:1 with Detail, 1:N with Course)
CREATE TABLE `instructor` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) DEFAULT NULL,
  `last_name` VARCHAR(45) DEFAULT NULL,
  `email` VARCHAR(45) DEFAULT NULL,
  `instructor_detail_id` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_DETAIL_idx` (`instructor_detail_id`),
  CONSTRAINT `FK_INSTRUCTOR_DETAIL` FOREIGN KEY (`instructor_detail_id`) 
    REFERENCES `instructor_detail` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 3. Course Table (N:1 with Instructor, 1:N with Review, M:N with Student)
CREATE TABLE `course` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(128) DEFAULT NULL,
  `course_code` VARCHAR(45) DEFAULT NULL,
  `credits` INT DEFAULT 3,
  `instructor_id` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `TITLE_UNIQUE` (`title`),
  KEY `FK_INSTRUCTOR_idx` (`instructor_id`),
  CONSTRAINT `FK_COURSE_INSTRUCTOR` FOREIGN KEY (`instructor_id`) 
    REFERENCES `instructor` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 4. Review Table (Unidirectional 1:N with Course)
CREATE TABLE `review` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `rating` INT DEFAULT 5,
  `comment` VARCHAR(256) DEFAULT NULL,
  `course_id` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_COURSE_ID_idx` (`course_id`),
  CONSTRAINT `FK_REVIEW_COURSE` FOREIGN KEY (`course_id`) 
    REFERENCES `course` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 5. Student Table (M:N with Course)
CREATE TABLE `student` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) DEFAULT NULL,
  `last_name` VARCHAR(45) DEFAULT NULL,
  `email` VARCHAR(45) DEFAULT NULL,
  `major` VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 6. Course-Student Join Table (M:N Association Table)
CREATE TABLE `course_student` (
  `course_id` INT NOT NULL,
  `student_id` INT NOT NULL,
  PRIMARY KEY (`course_id`, `student_id`),
  KEY `FK_STUDENT_idx` (`student_id`),
  CONSTRAINT `FK_JOIN_COURSE` FOREIGN KEY (`course_id`) 
    REFERENCES `course` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_JOIN_STUDENT` FOREIGN KEY (`student_id`) 
    REFERENCES `student` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
