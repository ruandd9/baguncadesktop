# TodoBagunça - Kanban Board

Um aplicativo de quadro Kanban desenvolvido como projeto acadêmico durante o curso Técnico em Desenvolvimento de Sistemas no SENAC, na Unidade Curricular de Desenvolvimento Desktop com Java.

## Sobre o Projeto

Este projeto foi desenvolvido em equipe por 5 estudantes, combinando conceitos de desenvolvimento desktop, interface gráfica com Java Swing, e gerenciamento de banco de dados. O resultado é uma aplicação Kanban moderna com tema escuro inspirado no Discord, demonstrando na prática os conhecimentos adquiridos durante o curso.

## Equipe de Desenvolvimento
- Desenvolvido por uma equipe de 5 estudantes no curso Técnico em Desenvolvimento de Sistemas - SENAC

## Funcionalidades

- Interface moderna com tema escuro inspirado no Discord
- Sistema de login e registro de usuários
- Registro de atividades em tempo real
- Gerenciamento de tarefas
- Armazenamento em banco de dados MySQL
- Menu de contexto para gerenciar tarefas
- Sistema de notificações
- Gerenciamento de equipes
- Checklists para tarefas

## Requisitos

- Java JDK 8 ou superior
- MySQL Server (recomendado usar XAMPP)
- NetBeans IDE (opcional)

## Configuração

1. Clone o repositório
```bash
git clone https://github.com/ruandd9/baguncadesktop
```

2. Configure o banco de dados
- Inicie o MySQL Server
- Execute o script SQL em `src/database/kanban_db.sql`

3. Credenciais padrão
- Email: admin@example.com
- Senha: admin123

## Tecnologias Utilizadas

- Java Swing - Interface gráfica
- MySQL - Banco de dados
- JDBC - Conexão com banco de dados (https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.0.0/mysql-connector-j-9.0.0.jar)
- JCalendar - Seleção de datas (https://repo1.maven.org/maven2/com/toedter/jcalendar/1.4/jcalendar-1.4.jar)

## Estrutura do Projeto

- `src/javaapplication1/`
  - `JavaApplication1.java` - Classe principal e interface Kanban
  - `DatabaseManager.java` - Gerenciamento do banco de dados
  - `LoginDialog.java` - Tela de login
  - `RegisterDialog.java` - Tela de registro de usuário
  - `Task.java` - Modelo de tarefa
  - `User.java` - Modelo de usuário
  - `Team.java` - Modelo de equipe
  - `TeamDialog.java` - Tela de gerenciamento de equipe
  - `TeamsListDialog.java` - Lista de equipes disponíveis
  - `TeamNotification.java` - Modelo de notificação de equipe
  - `NotificationsDialog.java` - Tela de notificações
  - `ChecklistDialog.java` - Tela de gerenciamento de checklist
  - `ChecklistItem.java` - Modelo de item de checklist
  - `TaskDialog.java` - Tela de criação/edição de tarefa
- `src/database/`
  - `kanban_db.sql` - Script de criação do banco de dados

## Agradecimentos

Agradecemos ao SENAC e aos professores da UC de Desenvolvimento Desktop com Java pelo suporte e orientação durante o desenvolvimento deste projeto.
