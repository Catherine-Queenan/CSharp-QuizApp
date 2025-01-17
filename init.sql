CREATE TABLE users (
    id BINARY(16), 
    username VARCHAR (20), 
    password VARCHAR(100), 
    role CHAR(1),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    name VARCHAR(50) PRIMARY KEY
);

CREATE TABLE quizzes (
    name VARCHAR(100) PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    description TEXT,
    FOREIGN KEY (category_name) REFERENCES categories(name)
);

CREATE INDEX idx_quizzes_category_name ON quizzes(category_name);

CREATE TABLE tags (
    name VARCHAR(20) PRIMARY KEY
);

CREATE TABLE quiz_tags (
    quiz_name VARCHAR(100),
    tag_name VARCHAR(20),
    PRIMARY KEY (quiz_name, tag_name),
    FOREIGN KEY (quiz_name) REFERENCES quizzes(name) ON DELETE CASCADE,
    FOREIGN KEY (tag_name) REFERENCES tags(name) ON DELETE CASCADE
);

CREATE INDEX idx_quiz_tags_quiz_name ON quiz_tags(quiz_name);
CREATE INDEX idx_quiz_tags_tag_name ON quiz_tags(tag_name);

CREATE TABLE questions (
    id BINARY(16) PRIMARY KEY,
    quiz_name VARCHAR(100) NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (quiz_name) REFERENCES quizzes(name) ON DELETE CASCADE
);

CREATE INDEX idx_questions_quiz_name ON questions(quiz_name);

CREATE TABLE answers (
    id BINARY(16) PRIMARY KEY,
    question_id BINARY(16) NOT NULL,
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    answer_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE INDEX idx_answers_question_id ON answers(question_id);

CREATE TABLE media (
    id BINARY(16) PRIMARY KEY,
    description TEXT,
    media_type TEXT,
    media_file_path TEXT,
    media_filename TEXT,
    media_start TEXT,
    media_end TEXT
);

CREATE TABLE question_media (
    question_id BINARY(16),
    media_id BINARY(16),
    PRIMARY KEY (question_id, media_id),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);

CREATE INDEX idx_question_media_question_id ON question_media(question_id);
CREATE INDEX idx_question_media_media_id ON question_media(media_id);

CREATE TABLE quiz_media (
    quiz_name VARCHAR(100),
    media_id BINARY(16),
    PRIMARY KEY (quiz_name, media_id),
    FOREIGN KEY (quiz_name) REFERENCES quizzes(name) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);

CREATE INDEX idx_quiz_media_quiz_name ON quiz_media(quiz_name);
CREATE INDEX idx_quiz_media_media_id ON quiz_media(media_id);

CREATE TABLE answer_media (
    answer_id BINARY(16),
    media_id BINARY(16),
    PRIMARY KEY (answer_id, media_id),
    FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);

CREATE INDEX idx_answer_media_answer_id ON answer_media(answer_id);
CREATE INDEX idx_answer_media_media_id ON answer_media(media_id);

CREATE TABLE category_media (
    category_name VARCHAR(50),
    media_id BINARY(16),
    PRIMARY KEY (category_name, media_id),
    FOREIGN KEY (category_name) REFERENCES categories(name) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);

CREATE INDEX idx_category_media_category_name ON category_media(category_name);
CREATE INDEX idx_category_media_media_id ON category_media(media_id);
CREATE UNIQUE INDEX idx_users_username ON users (username);

CREATE TABLE moderated_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    moderator_id VARCHAR(20),
    start_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time DATETIME,
    session_status ENUM('active', 'ended') NOT NULL DEFAULT 'active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    quiz_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (moderator_id) REFERENCES users(username) ON DELETE CASCADE
);

DELIMITER //

CREATE TRIGGER after_quiz_delete
AFTER DELETE ON quizzes
FOR EACH ROW
BEGIN
    DECLARE remaining_quizzes INT;

    -- Check if there are any remaining quizzes in the category
    SELECT COUNT(*) INTO remaining_quizzes
    FROM quizzes
    WHERE category_name = OLD.category_name;

    -- If no remaining quizzes, delete the category
    IF remaining_quizzes = 0 THEN
        DELETE FROM categories WHERE name = OLD.category_name;
    END IF;
END;

//

DELIMITER ;
