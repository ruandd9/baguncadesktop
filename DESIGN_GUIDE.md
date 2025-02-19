# Guia de Design - Kanban Board

Este guia explica os principais componentes visuais e métodos relacionados ao design da aplicação. Use-o como referência ao fazer modificações na interface.

## 1. Estrutura Principal (KanbanBoard.java)

### 1.1 Painéis Principais

- `initComponents()`: Inicializa o layout base
  - Painel principal: Layout BorderLayout
  - Painel superior: Contém título e botões
  - Painel central: Contém as colunas do Kanban
  - Painel inferior: Contém o log de atividades

### 1.2 Colunas do Kanban

- `createColumnPanel(String title)`: Cria uma coluna do Kanban
  - Layout: BoxLayout (Y_AXIS)
  - Título da coluna
  - Área de tarefas (JPanel com scroll)
  - Cores e bordas personalizáveis

### 1.3 Tarefas

- `createTaskPanel(int id, String name, String priority, Date dueDate)`: Cria o painel de uma tarefa
  - Layout: BorderLayout
  - Título da tarefa
  - Prioridade (com ícone colorido)
  - Data de entrega
  - Menu de contexto (botão direito)
  - Cores baseadas na prioridade

### 1.4 Menu de Contexto

- `createTaskContextMenu(JPanel taskPanel)`: Cria o menu de contexto da tarefa
  - Opções: Mover e Excluir
  - Ícones e atalhos de teclado
  - Estilo consistente com o tema

## 2. Diálogo de Tarefas (TaskDialog.java)

### 2.1 Formulário

- `initComponents()`: Inicializa o formulário
  - Campos de entrada
  - Seletor de prioridade
  - Seletor de data
  - Botões de ação

### 2.2 Validação

- `validateFields()`: Valida os campos do formulário
  - Mensagens de erro
  - Destaque visual de campos inválidos

## 3. Personalização Visual

### 3.1 Cores Padrão
```java
// Cores do tema
static final Color BACKGROUND_COLOR = new Color(36, 41, 46);
static final Color PANEL_COLOR = new Color(47, 54, 61);
static final Color TEXT_COLOR = new Color(201, 209, 217);

// Cores de prioridade
static final Color PRIORITY_HIGH = new Color(215, 58, 73);
static final Color PRIORITY_MEDIUM = new Color(251, 189, 35);
static final Color PRIORITY_LOW = new Color(63, 185, 80);
```

### 3.2 Estilos de Componentes

#### Painéis
```java
private void stylePanel(JPanel panel) {
    panel.setBackground(PANEL_COLOR);
    panel.setBorder(BorderFactory.createLineBorder(new Color(48, 54, 61)));
}
```

#### Botões
```java
private void styleButton(JButton button) {
    button.setBackground(new Color(47, 129, 247));
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
}
```

#### Labels
```java
private void styleLabel(JLabel label) {
    label.setForeground(TEXT_COLOR);
    label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
}
```

## 4. Principais Métodos para Customização

### 4.1 KanbanBoard.java

- `initializeUI()`: Configura o tema geral da aplicação
- `createColumnHeader(String title)`: Estilo do cabeçalho da coluna
- `styleTaskPanel(JPanel panel, String priority)`: Aplica estilo baseado na prioridade
- `createPriorityLabel(String priority)`: Cria ícone de prioridade
- `formatDueDate(Date date)`: Formata a exibição da data

### 4.2 TaskDialog.java

- `setupTheme()`: Configura o tema do diálogo
- `setupPriorityComboBox()`: Configura o visual do seletor de prioridade
- `setupDatePicker()`: Configura o visual do seletor de data

## 5. Dicas para Modificações

1. **Consistência**
   - Mantenha as cores consistentes com as variáveis definidas
   - Use as mesmas fontes em toda a aplicação
   - Mantenha o espaçamento padronizado

2. **Performance**
   - Evite criar novos objetos de cor/fonte repetidamente
   - Use variáveis estáticas para recursos compartilhados
   - Minimize o uso de imagens grandes

3. **Acessibilidade**
   - Mantenha bom contraste entre texto e fundo
   - Use tamanhos de fonte legíveis
   - Adicione tooltips informativos

4. **Responsividade**
   - Use layouts que se adaptam ao tamanho da janela
   - Defina tamanhos mínimos para componentes
   - Teste em diferentes resoluções

## 6. Exemplos de Customização

### 6.1 Mudar Tema de Cores
```java
// Em KanbanBoard.java
public void setCustomTheme(Color background, Color panel, Color text) {
    BACKGROUND_COLOR = background;
    PANEL_COLOR = panel;
    TEXT_COLOR = text;
    refreshUI();
}
```

### 6.2 Customizar Tarefa
```java
// Em KanbanBoard.java
private void customizeTaskPanel(JPanel panel) {
    panel.setPreferredSize(new Dimension(200, 100));
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(48, 54, 61)),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));
}
```

### 6.3 Adicionar Ícones
```java
// Em KanbanBoard.java
private void addTaskIcons(JPanel panel) {
    JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    iconPanel.setOpaque(false);
    iconPanel.add(new JLabel(new ImageIcon("path/to/icon.png")));
    panel.add(iconPanel, BorderLayout.EAST);
}
```
