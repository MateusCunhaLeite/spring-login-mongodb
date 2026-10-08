# Login Seguro

Sistema de **cadastro, login e controle de acesso por perfil** feito com **Java 21, Spring Boot 4, Spring Security, Thymeleaf e MongoDB Atlas**.

Projeto acadêmico individual, pensado para ser simples, organizado e reaproveitável: o modelo de perfis, de usuário e a identidade visual foram desenhados para servir de base ao PFC *Athena – O Portal do PFC*.

| Tema `default` | Tema `athena` |
|---|---|
| ![Tela inicial com o tema default](docs/img/inicio-default.png) | ![Tela inicial com o tema athena](docs/img/inicio-athena.png) |

---

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias e versões](#tecnologias-e-versões)
- [Pré-requisitos](#pré-requisitos)
- [Configurando o MongoDB Atlas](#configurando-o-mongodb-atlas)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Como rodar](#como-rodar)
- [Perfis e rotas](#perfis-e-rotas)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Temas visuais](#temas-visuais)
- [Segurança](#segurança)
- [Sessões no MongoDB](#sessões-no-mongodb)
- [Adaptação ao PFC (Cognito/JWT)](#adaptação-ao-pfc-cognitojwt)
- [Fluxo de trabalho (Gitflow)](#fluxo-de-trabalho-gitflow)
- [Trabalhos futuros](#trabalhos-futuros)

---

## Funcionalidades

- **Cadastro público** com validação (mensagens em português ao lado de cada campo). Todo cadastro público cria um **ALUNO**.
- **Login e logout** com formulário próprio, senha guardada com **BCrypt** e proteção **CSRF**.
- **Três perfis** (ALUNO, ORIENTADOR e ADMIN), cada um com sua área e acesso controlado pelo Spring Security.
- **Painel do administrador**: lista de usuários, criação de orientadores e administradores e ativação/desativação de contas.
- **Primeiro administrador criado automaticamente** a partir de variáveis de ambiente.
- **Usuários e sessões no MongoDB Atlas** (coleções `users` e `sessions`).
- **Dois temas visuais** (`default` e `athena`), trocados com uma linha de configuração.
- **Restrição opcional de domínio** no cadastro (ex.: só e-mails `@umc.br`).

## Tecnologias e versões

| Tecnologia | Versão | Para quê |
|---|---|---|
| Java | 21 (LTS) | Linguagem |
| Spring Boot | 4.0.8 | Base da aplicação |
| Spring Security | 7.0 | Autenticação e autorização |
| Thymeleaf + Layout Dialect | 3.1 / 3.4 | Páginas HTML e layout reutilizável |
| Spring Data MongoDB | 5.0 (release train 2025.1) | Acesso ao banco |
| mongodb-spring-session | 4.0.0 | Sessões HTTP no MongoDB |
| Maven Wrapper | 3.9 | Build (não precisa instalar o Maven) |

**Por que Spring Boot 4.0.x (e não 4.1 ou 3.5)?** O módulo de sessões no MongoDB deixou de ser mantido pelo time do Spring: o antigo `spring-session-data-mongodb` parou na versão 3.5, e o projeto passou para a MongoDB Inc. com o nome `org.mongodb:mongodb-spring-session`. A versão 4.0.0 desse artefato foi compilada com Spring Session 4.0, Spring Security 7.0 e Spring Data 2025.1, exatamente as versões que o **Spring Boot 4.0.x** traz. O Boot 3.5 já saiu do suporte gratuito. Como o Boot 4 não configura mais essas sessões sozinho, o projeto usa `@EnableMongoHttpSession` (veja `SessionConfig`).

## Pré-requisitos

- **Java 21** (no Ubuntu: `sudo apt install -y openjdk-21-jdk`). Confira com `java -version`.
- **Git**.
- **Conta gratuita no [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register)**.

Não é preciso instalar o Maven: o projeto usa o Maven Wrapper (`./mvnw`).

## Configurando o MongoDB Atlas

1. **Crie um cluster gratuito.** No Atlas: *Create* → plano **Free (M0)** → provedor **AWS**, região **São Paulo (sa-east-1)** → *Create Deployment*. Desmarque *Preload sample dataset* (não precisamos de dados de exemplo).
2. **Crie um usuário do banco.** Em *Security → Database Access → Add New Database User*:
   - autenticação por senha; use *Autogenerate Secure Password* (só letras e números, o que evita problemas na URL);
   - permissão **Read and write to any database** (menor privilégio: a aplicação não precisa administrar o cluster).
3. **Libere o seu IP.** Em *Security → Network Access → IP Access List → Add IP Address*, adicione o IP atual.
   - Se a sua internet usar **mais de um IP de saída**, libere todos. O sintoma de IP não liberado é o erro `SSLException: Received fatal alert: internal_error`, às vezes intermitente. Para ver o IP atual: `curl https://checkip.amazonaws.com`.
   - Evite `0.0.0.0/0` (liberar para qualquer IP).
4. **Copie a connection string.** No cluster: *Connect → Drivers → Java*. Ela tem o formato:
   ```
   mongodb+srv://USUARIO:<db_password>@cluster0.xxxxx.mongodb.net/?appName=Cluster0
   ```
   Troque `<db_password>` pela senha (sem `< >`) e **acrescente o nome do banco** entre a `/` e o `?`:
   ```
   mongodb+srv://USUARIO:SENHA@cluster0.xxxxx.mongodb.net/login_seguro?appName=Cluster0
   ```
5. **Coloque a string no arquivo `.env`** (próxima seção). O banco `login_seguro` e as coleções são criados sozinhos na primeira execução.

A conexão com o Atlas é sempre criptografada (TLS), já que o formato `mongodb+srv://` habilita TLS por padrão.

## Variáveis de ambiente

Nenhum segredo fica no código nem no Git. O `application.yml` só lê variáveis, e o arquivo `.env` (que está no `.gitignore`) é carregado automaticamente pela linha `spring.config.import: optional:file:.env[.properties]`.

```bash
cp .env.example .env   # depois edite o .env com os seus valores
```

| Variável | Obrigatória | Descrição |
|---|---|---|
| `MONGODB_URI` | Sim | Connection string do Atlas, com o nome do banco |
| `ADMIN_EMAIL` | Recomendada | E-mail do primeiro administrador (criado na inicialização, se não existir) |
| `ADMIN_PASSWORD` | Recomendada | Senha desse administrador (8+ caracteres, com letras e números) |
| `APP_THEME` | Não | `default` (padrão) ou `athena` |
| `APP_ALLOWED_DOMAIN` | Não | Se preenchida (ex.: `umc.br`), o cadastro só aceita e-mails desse domínio e de subdomínios (`alunos.umc.br`) |

Variáveis definidas no próprio terminal têm prioridade sobre o `.env`. Exemplo: `APP_THEME=athena ./mvnw spring-boot:run`.

## Como rodar

```bash
./mvnw spring-boot:run      # sobe a aplicação em http://localhost:8080
./mvnw test                 # roda os testes (não precisam do Atlas)
./mvnw package              # gera target/login-seguro-<versão>.jar
java -jar target/login-seguro-*.jar
```

Na primeira execução, o log mostra `Administrador inicial criado: <seu e-mail>`. Entre com esse e-mail e a senha do `.env`.

## Perfis e rotas

| Rota | Quem acessa | O que tem |
|---|---|---|
| `/` | Todos | Página inicial |
| `/login`, `/cadastro` | Todos | Login e cadastro público (cria ALUNO) |
| `/dashboard` | Logados | Redireciona para a área do perfil |
| `/aluno/**` | ALUNO | Dados da conta |
| `/orientador/**` | ORIENTADOR | Lista dos alunos cadastrados (somente leitura) |
| `/admin/**` | ADMIN | Usuários, criação de ORIENTADOR/ADMIN, ativar/desativar contas |

- Quem tenta acessar uma área de outro perfil vê a página amigável **403 – Acesso negado**.
- Visitantes não logados são enviados para o `/login`.
- **ORIENTADOR** só é criado pelo ADMIN. O **primeiro ADMIN** vem das variáveis de ambiente.
- Conta **INATIVA** não consegue entrar, e o admin não pode desativar a si mesmo.

## Estrutura do projeto

O código é organizado **por funcionalidade**, não por tipo de classe:

```
src/main/java/com/mateusleite/loginseguro/
├── config/   SecurityConfig, SessionConfig, AppProperties, AdminSeeder, Tema, ThemeAdvice
├── user/     User, Role, Status, UserRepository, UserService
├── auth/     AuthController, RegisterForm, RegisterValidator, @SenhaForte,
│             MongoUserDetailsService, UsuarioLogado
└── web/      HomeController, DashboardController, AlunoController,
              OrientadorController, AdminController, NovoUsuarioForm

src/main/resources/
├── application.yml            configuração sem segredos
├── templates/
│   ├── layouts/base.html      layout principal (Thymeleaf Layout Dialect)
│   ├── fragments/             header, footer e alerts
│   ├── pages/                 index, login, cadastro, aluno, orientador, admin
│   └── error/                 403, 404 e erro genérico
└── static/
    ├── css/base.css           estrutura e componentes (sem cores fixas)
    └── themes/
        ├── default/theme.css  variáveis do tema neutro
        └── athena/theme.css   variáveis do tema Athena
```

## Temas visuais

O design fica separado da lógica. Os templates usam apenas **classes semânticas** (`.card`, `.btn-primary`, `.alert-error`...), e o `base.css` monta a estrutura com **variáveis CSS**. Cada tema define só os valores dessas variáveis (cores, fontes, cantos).

**Para trocar de tema**, mude uma linha no `.env` e reinicie:
```
APP_THEME=athena
```

| Tema | Visual |
|---|---|
| `default` | Neutro e moderno, com fontes do sistema e cantos arredondados |
| `athena` | Azul-marinho `#23398A` e bronze `#A97A46`, fundo creme, Playfair Display + Inter, cantos retos |

**Para criar um tema novo:** crie `static/themes/<nome>/theme.css` com as mesmas variáveis do tema `default` e adicione uma constante em `config/Tema.java` (nome e subtítulo exibidos no cabeçalho).

![Painel do administrador no tema Athena](docs/img/admin-athena.png)

## Segurança

| Decisão | Por quê |
|---|---|
| Senhas com **BCrypt** | Hash lento e com "sal": nem o banco nem o admin conhecem as senhas |
| **CSRF** habilitado | Todo formulário POST leva um token secreto, que o Thymeleaf injeta sozinho |
| **Logout via POST** | Um link malicioso não consegue deslogar o usuário |
| Role **nunca** vem do formulário | O cadastro público sempre cria ALUNO, mesmo que alguém adultere a requisição |
| "Conta inativa" só **após a senha correta** | Quem não sabe a senha não descobre se a conta existe ou está inativa |
| Mesma mensagem para e-mail inexistente e senha errada | Evita descobrir quais e-mails estão cadastrados |
| E-mail **único** (índice no MongoDB) e normalizado em minúsculas | Impede contas duplicadas, até em cadastros simultâneos |
| Segredos só no `.env` | Nada sensível vai para o Git; só o `.env.example` é versionado |
| Usuário do Atlas com **menor privilégio** e **IPs liberados** | Reduz o estrago se uma credencial vazar |
| Minimização de dados (LGPD) | Coletamos só nome, e-mail e senha; nada de CPF ou telefone |

## Sessões no MongoDB

As sessões HTTP ficam na coleção **`sessions`** do Atlas, e não na memória do servidor. Por isso o usuário **continua logado mesmo se a aplicação reiniciar**, e várias instâncias podem compartilhar as sessões. O cookie do navegador passa a se chamar `SESSION`.

**Para ver no Atlas:**
1. Faça login na aplicação.
2. No Atlas: *Data Explorer* → `Cluster0` → `login_seguro` → **`sessions`** → aba *Documents*. Há um documento por sessão ativa, com o campo `expireAt`.
3. Clique em **Sair** na aplicação e atualize a coleção: o documento some.
4. Na aba *Indexes* há um **índice TTL** em `expireAt`. É ele que faz o MongoDB apagar sozinho as sessões vencidas (30 minutos sem uso).

## Adaptação ao PFC (Cognito/JWT)

As regras de acesso dependem **só da role**, não de **como** o usuário se autenticou. Hoje a autenticação é feita por formulário; no PFC (AWS Cognito) ela pode ser feita com **OIDC/JWT**, com a role vinda do claim `custom:perfil`. A troca fica concentrada no `SecurityConfig`:
- substituir `formLogin(...)` por `oauth2Login(...)` (OIDC) ou `oauth2ResourceServer(jwt)` (API);
- converter o claim `custom:perfil` em `ROLE_ALUNO`, `ROLE_ORIENTADOR` ou `ROLE_ADMIN`.

As regras `hasRole(...)`, os controllers e as páginas continuam iguais. O que mais se reaproveita do projeto é o **modelo**: os perfis, os campos do usuário, as regras de cadastro e o tema Athena.

## Fluxo de trabalho (Gitflow)

- `main`: versões publicadas (tags `vX.Y.Z`).
- `develop`: integração.
- `feature/*`: uma branch por etapa, juntada na `develop` com `--no-ff`.
- `release/*`: preparação de versão, juntada na `main` e na `develop`.

Commits seguem o padrão *Conventional Commits* (`feat:`, `fix:`, `docs:`, `test:`...). Para ver o histórico: `git log --oneline --graph --all`.

## Trabalhos futuros

- Confirmação de e-mail (o campo `emailConfirmado` já existe e hoje é sempre `false`).
- Recuperação e troca de senha pelo próprio usuário.
- Encerrar as sessões ativas de um usuário no momento em que ele é desativado.
- Limite de tentativas de login (proteção contra força bruta).
- Login via AWS Cognito (OIDC), como descrito acima.

---

Desenvolvido por **Mateus Leite** como atividade acadêmica.
