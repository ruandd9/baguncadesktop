-- Criar o banco de dados
DROP DATABASE IF EXISTS kanban_db;
CREATE DATABASE kanban_db;
USE kanban_db;

-- Criar a tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criar a tabela de tarefas
CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    column_name VARCHAR(50) NOT NULL,
    created_by INT NOT NULL,
    priority ENUM('ALTA', 'MEDIA', 'BAIXA') DEFAULT 'MEDIA',
    due_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Criar a tabela de atividades
CREATE TABLE IF NOT EXISTS activities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    task_id INT,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    source_column VARCHAR(50),
    target_column VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
);

-- Inserir um usuário de exemplo
INSERT INTO users (name, email, password) VALUES
    ('Admin', 'admin@example.com', 'admin123');

-- Inserir algumas tarefas de exemplo
INSERT INTO tasks (name, column_name, created_by, priority, due_date) VALUES
    ('Implementar login', 'A Fazer', 1, 'ALTA', DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY)),
    ('Desenvolver interface', 'Fazendo', 1, 'MEDIA', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY)),
    ('Testar aplicação', 'A Fazer', 1, 'BAIXA', DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY));
