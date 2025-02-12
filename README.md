# Kanban Board

Um aplicativo de quadro Kanban desenvolvido em Java Swing com tema escuro inspirado no Discord.

## Funcionalidades

- Interface moderna com tema escuro
- Sistema de login de usuários
- Registro de atividades em tempo real
- Click and drop de tarefas entre colunas
- Armazenamento em banco de dados MySQL
- Menu de contexto para gerenciar tarefas

## Requisitos

- Java JDK 8 ou superior
- MySQL Server (recomendado usar XAMPP)
- NetBeans IDE (opcional)

## Configuração

1. Clone o repositório
```bash
git clone [URL_DO_SEU_REPOSITORIO]
```

2. Configure o banco de dados
- Inicie o MySQL Server
- Execute o script SQL em `src/database/kanban_db.sql`

3. Credenciais padrão
- Email: admin@example.com
- Senha: admin123

## Tecnologias

- Java Swing
- MySQL
- JDBC - https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.0.0/mysql-connector-j-9.0.0.jar - Calendar - https://repo1.maven.org/maven2/com/toedter/jcalendar/1.4/jcalendar-1.4.jar

## Estrutura do Projeto

- `src/javaapplication1/`
  - `JavaApplication1.java` - Classe principal e interface Kanban
  - `DatabaseManager.java` - Gerenciamento do banco de dados
  - `LoginDialog.java` - Tela de login
  - `Task.java` - Modelo de tarefa
  - `User.java` - Modelo de usuário
- `src/database/`
  - `kanban_db.sql` - Script de criação do banco de dados
