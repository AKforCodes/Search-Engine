CREATE DATABASE search_engine;

USE search_engine;

CREATE TABLE documents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(2083) NOT NULL UNIQUE,
    title VARCHAR(255),
    content TEXT
);

CREATE TABLE words (
    id INT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE inverted_index (
    word_id INT,
    document_id INT,
    frequency INT,
    PRIMARY KEY (word_id, document_id),
    FOREIGN KEY (word_id) REFERENCES words(id),
    FOREIGN KEY (document_id) REFERENCES documents(id)
);
