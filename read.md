<h1 align="center">🌟 TodoBagunça - Kanban Board 🌟</h1>

<p align="center">
  <strong>Um aplicativo de quadro Kanban</strong> desenvolvido como projeto acadêmico durante o curso Técnico em Desenvolvimento de Sistemas no SENAC, na Unidade Curricular de Desenvolvimento Desktop com Java. 🚀
</p>

<p align="center">
  <a href="https://shields.io"><img src="https://img.shields.io/badge/Status-Concluído-green?style=flat-square" alt="Status: Concluído"></a>
  <a href="https://shields.io"><img src="https://img.shields.io/badge/Licença-MIT-blue?style=flat-square" alt="Licença: MIT"></a>
  <a href="https://shields.io"><img src="https://img.shields.io/badge/Projeto-TodoBagunça-orange?style=for-the-badge" alt="Projeto TodoBagunça"></a>
</p>

---

<h2 align="center">📖 Sobre o Projeto</h2>

<p align="center">
  <img src="https://img.shields.io/badge/🔥-Organize_suas_Tarefas-red?style=for-the-badge" alt="Organize suas Tarefas">
</p>

O **TodoBagunça** é uma aplicação desktop de quadro Kanban desenvolvida por uma equipe de 5 estudantes do curso Técnico em Desenvolvimento de Sistemas no SENAC. Com uma interface moderna de tema escuro inspirada no Discord, a aplicação combina conceitos de desenvolvimento desktop, interface gráfica com **Java Swing**, e gerenciamento de banco de dados com **MySQL**. O projeto demonstra na prática os conhecimentos adquiridos na Unidade Curricular de Desenvolvimento Desktop com Java.

---

<h2 align="center">👥 Equipe de Desenvolvimento</h2>

| 🧑‍💻 Membro | Papel |
|-------------|-------|
| **[Nome do Membro 1]** | Desenvolvedor |
| **[Nome do Membro 2]** | Desenvolvedor |
| **[Nome do Membro 3]** | Desenvolvedor |
| **[Nome do Membro 4]** | Desenvolvedor |
| **[Nome do Membro 5]** | Desenvolvedor |

> **Nota**: Substitua os placeholders **[Nome do Membro X]** pelos nomes reais dos membros da equipe.

---

<h2 align="center">🛠️ Funcionalidades</h2>

<p align="center">
  <img src="https://img.shields.io/badge/✅-Funcionalidades_Implementadas-blueviolet?style=for-the-badge" alt="Funcionalidades Implementadas">
</p>

| 🟢 Funcionalidade | Descrição |
|-------------------|-----------|
| 🎨 Interface Moderna | Tema escuro inspirado no Discord para uma experiência visual agradável. |
| 🔐 Login e Registro | Sistema de autenticação com cadastro de usuários. |
| ⏰ Registro em Tempo Real | Acompanhamento de atividades em tempo real. |
| 📋 Gerenciamento de Tarefas | Crie, edite e organize tarefas em um quadro Kanban. |
| 🗄️ Banco de Dados MySQL | Armazenamento seguro de dados com MySQL. |
| 📍 Menu de Contexto | Gerencie tarefas diretamente com menus contextuais. |
| 🔔 Notificações | Sistema de notificações para atualizações importantes. |
| 👥 Gerenciamento de Equipes | Crie e gerencie equipes para colaboração. |
| ✅ Checklists | Adicione checklists às tarefas para maior organização. |

---

<h2 align="center">📋 Requisitos</h2>

Para executar o **TodoBagunça**, você precisará de:

- ☕ **Java JDK 8** ou superior
- 🗄️ **MySQL Server** (recomendado usar XAMPP)
- 🛠️ **NetBeans IDE** (opcional, para desenvolvimento)

---

<h2 align="center">⚙️ Configuração</h2>

<p align="center">
  <img src="https://img.shields.io/badge/🚀-Configure_em_Minutos-brightgreen?style=for-the-badge" alt="Configure em Minutos">
</p>

1. **Clone o repositório**:
   ```bash
   git clone [URL_DO_SEU_REPOSITORIO]
 herni
2. **Configure o banco de dados**:
   - Inicie o **MySQL Server** (ex.: via XAMPP).
   - Execute o script SQL localizado em `src/database/kanban_db.sql`.
3. **Credenciais padrão**:
   - **Email**: `admin@example.com`
   - **Senha**: `admin123`
4. **Execute a aplicação**:
   - Abra o projeto no NetBeans ou compile diretamente com o Java.

---

<h2 align="center">💻 Tecnologias Utilizadas</h2>

<p align="center">
  <a href="https://shields.io"><img src="https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white" alt="Java"></a>
  <a href="https://shields.io"><img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL"></a>
  <a href="https://shields.io"><img src="https://img.shields.io/badge/JDBC-000000?style=flat-square&logo=java&logoColor=white" alt="JDBC"></a>
  <a href="https://shields.io"><img src="https://img.shields.io/badge/JCalendar-FFD700?style=flat-square&logo=java&logoColor=black" alt="JCalendar"></a>
</p>

- ☕ **Java Swing**: Interface gráfica moderna.
- 🗄️ **MySQL**: Banco de dados relacional para armazenamento.
- 🔗 **JDBC**: Conexão com o banco de dados ([MySQL Connector](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.0.0/mysql-connector-j-9.0.0.jar)).
- 📅 **JCalendar**: Seleção de datas ([JCalendar](https://repo1.maven.org/maven2/com/toedter/jcalendar/1.4/jcalendar-1.4.jar)).

---

<h2 align="center">📂 Estrutura do Projeto</h2>

```plaintext
src/
  └── javaapplication1/
      ├── JavaApplication1.java      # Classe principal e interface Kanban
      ├── DatabaseManager.java       # Gerenciamento do banco de dados
      ├── LoginDialog.java           # Tela de login
      ├── RegisterDialog.java        # Tela de registro de usuário
      ├── Task.java                  # Modelo de tarefa
      ├── User.java                  # Modelo de usuário
      ├── Team.java                  # Modelo de equipe
      ├── TeamDialog.java            # Tela de gerenciamento de equipe
      ├── TeamsListDialog.java       # Lista de equipes disponíveis
      ├── TeamNotification.java      # Modelo de notificação de equipe
      ├── NotificationsDialog.java   # Tela de notificações
      ├── ChecklistDialog.java       # Tela de gerenciamento de checklist
      ├── ChecklistItem.java         # Modelo de item de checklist
      ├── TaskDialog.java            # Tela de criação/edição de tarefa
  └── database/
      ├── kanban_db.sql              # Script de criação do banco de dados


📸 Prévia da Aplicação


  



Nota: Substitua [URL_DA_IMAGEM_AQUI] pela URL de uma captura de tela da aplicação.


🙏 Agradecimentos


  


Agradecemos ao SENAC e aos professores da Unidade Curricular de Desenvolvimento Desktop com Java pelo suporte, orientação e oportunidade de desenvolver este projeto prático.

📧 Contato

Para dúvidas, sugestões ou feedback, entre em contato:

📩 Email: seu_email@example.com
🌐 Repositório: URL_DO_SEU_REPOSITORIO


⭐ Obrigado por conhecer o TodoBagunça! ⭐


  



  Experimente o **TodoBagunça** e transforme a gestão de tarefas em uma experiência prática e colaborativa! 🎉

```
