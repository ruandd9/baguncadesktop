```markdown
# 🌟 TodoBagunça Desktop - Projeto Integrador SENAC-DF 🌟

**Bem-vindo ao repositório do TodoBagunça**, uma aplicação desktop com quadro Kanban desenvolvida no SENAC-DF, como parte do Projeto Integrador da Unidade Curricular de Desenvolvimento Desktop com Java. 💻

![Status](https://img.shields.io/badge/Status-Concluído-green?style=flat-square)
![Projeto](https://img.shields.io/badge/Projeto-TodoBagunça-orange?style=for-the-badge)

---

## 📖 Descrição do Projeto

O **TodoBagunça** é uma aplicação desktop de organização pessoal com foco em produtividade e colaboração em equipe. Inspirado em ferramentas como o Trello e no visual do Discord, o projeto tem como base metodologias ágeis (Scrum e Kanban) e foi construído em **Java Swing**, com integração ao **MySQL** via **JDBC**.

Seu principal objetivo é auxiliar estudantes e profissionais no gerenciamento visual de tarefas em quadros, listas e checklists.

---

## 🛠️ Funcionalidades

### ✅ Funcionalidades Implementadas

| Funcionalidade             | Descrição                                                                 |
|---------------------------|---------------------------------------------------------------------------|
| 🎨 Interface Moderna      | Tema escuro inspirado no Discord para uma melhor experiência visual.     |
| 🔐 Login e Registro       | Sistema de autenticação com cadastro de usuários.                         |
| ⏰ Atualização em Tempo Real | Visualização de atividades e tarefas dinamicamente.                  |
| 📋 Gerenciamento de Tarefas | Crie, edite e mova tarefas entre colunas (Kanban).                   |
| 🗄️ Banco de Dados MySQL   | Armazenamento persistente com MySQL.                                     |
| 📍 Menu de Contexto       | Ações rápidas por clique direito nas tarefas.                            |
| 🔔 Notificações           | Informações e alertas importantes para os usuários.                      |
| 👥 Equipes                | Criação e gerenciamento de times colaborativos.                          |
| ✅ Checklists             | Adição de subtarefas dentro de tarefas principais.                       |

---

## 📋 Requisitos

Para executar o **TodoBagunça**, você precisará de:

- ☕ Java JDK 8 ou superior  
- 🗄️ MySQL Server (pode usar XAMPP)  
- 🛠️ NetBeans IDE (opcional, para desenvolvimento)

---

## ⚙️ Como Executar

### 🚀 Configure em Minutos

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/ruandd9/baguncadesktop.git
   ```

2. **Configure o banco de dados**:
   - Inicie o MySQL Server
   - Execute o script `src/database/kanban_db.sql`

3. **Credenciais padrão**:
   - Email: `admin@example.com`
   - Senha: `admin123`

4. **Execute a aplicação**:
   - Abra o projeto no NetBeans ou compile via terminal:
     ```bash
     javac -cp . JavaApplication1.java
     java JavaApplication1
     ```

---

## 💻 Tecnologias Utilizadas

![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-000000?style=flat-square&logo=java&logoColor=white)
![JCalendar](https://img.shields.io/badge/JCalendar-FFD700?style=flat-square&logo=java&logoColor=black)

- Java Swing: Interface gráfica responsiva  
- MySQL: Banco de dados relacional  
- JDBC: Conexão Java ↔ Banco ([MySQL Connector](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.0.0/mysql-connector-j-9.0.0.jar))  
- JCalendar: Componente de data ([JCalendar 1.4](https://repo1.maven.org/maven2/com/toedter/jcalendar/1.4/jcalendar-1.4.jar))

---

## 📂 Estrutura do Projeto

```plaintext
src/
  └── javaapplication1/
      ├── JavaApplication1.java      # Classe principal
      ├── DatabaseManager.java       # Conexão com o banco
      ├── LoginDialog.java           # Tela de login
      ├── RegisterDialog.java        # Tela de registro
      ├── Task.java                  # Modelo de tarefa
      ├── User.java                  # Modelo de usuário
      ├── Team.java                  # Modelo de equipe
      ├── TeamDialog.java            # Gerenciamento de equipe
      ├── TeamsListDialog.java       # Lista de equipes
      ├── TeamNotification.java      # Notificações de equipe
      ├── NotificationsDialog.java   # Tela de notificações
      ├── ChecklistDialog.java       # Gerenciador de checklists
      ├── ChecklistItem.java         # Item do checklist
      ├── TaskDialog.java            # Criar/editar tarefas
  └── database/
      └── kanban_db.sql              # Script SQL do banco
```

---

## 📸 Prévia da Interface

> Substitua o caminho abaixo por um arquivo de imagem real no repositório se quiser exibir a imagem

```markdown
![Interface TodoBagunça](preview-desktop.png)
```

---

## 👥 Contribuidores

- [Yan Fellippe](https://github.com/YanFellippe)  
- [Ruan Lobo](https://github.com/ruandd9)  
- [Douglas Oliveira](https://github.com/douglasarj)  
- Rafael  
- [Talisson Leandro](https://github.com/talissonleandro)

---

## 🙏 Agradecimentos

Agradecemos ao **SENAC-DF** e aos professores da Unidade Curricular de Desenvolvimento Desktop com Java pelo suporte, conhecimento compartilhado e oportunidade de realizar este projeto prático.

---

## 📧 Contato

Tem dúvidas, sugestões ou feedback? Fale com a gente:

- Email: [ruanoliveiralobo@gmail.com](mailto:ruanoliveiralobo@gmail.com)  
- GitHub: [github.com/ruandd9/baguncadesktop](https://github.com/ruandd9/baguncadesktop)

---

## ⭐ Obrigado por conhecer o TodoBagunça!

Experimente o **TodoBagunça** e transforme a gestão de tarefas em algo visual, colaborativo e intuitivo! 🚀
```
