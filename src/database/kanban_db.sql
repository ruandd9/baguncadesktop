-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS kanban_db;
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Criar a tabela de log de atividades
CREATE TABLE IF NOT EXISTS activity_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    task_id INT NOT NULL,
    action_type ENUM('CREATE', 'MOVE', 'EDIT', 'DELETE') NOT NULL,
    description VARCHAR(255) NOT NULL,
    old_value VARCHAR(255),
    new_value VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- Inserir um usuário de exemplo
INSERT INTO users (name, email, password) VALUES
    ('Admin', 'admin@example.com', 'admin123');

-- Inserir algumas tarefas de exemplo
INSERT INTO tasks (name, column_name, created_by) VALUES
    ('Implementar login', 'A Fazer', 1),
    ('Desenvolver interface', 'Fazendo', 1),
    ('Testar aplicação', 'A Fazer', 1);
