package com.example.algamoney.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@SpringBootApplication
/** Agora consegue usar a configuração definda em AlgamoneyApiProperty externamente.
 * Abrindo o application.properties, consegue acessar os atributos definidos em AlgamoneyApiProperty. 
 * Neste momento, pode definir outro arquivo: application-prod.properties, definindo: algamoney.seguranca.enable-https=true.
 * Nesse novo arquivo, -prod é o profile do Spring que consigo passar no momento que estou subindo a Aplicação. Então,
 * posso habilitar esse profile Produção somente quando estiver rodando no Heroku, por exemplo. Enquanto estiver rodando na máquina local,
 * continua a propriedade algamoney.seguranca.enable-https como false; quando estiver no Heroku, a propriedade é trocada para true, 
 * porque vai carregar o arquivo application-prod.properties.
 * 
 *  Primeiramente, carrega o application.properties, em seguida carrega application-prod.properties; vai deixar sempre o mais específico por último.
 *  
 *  Agora é só injetar AlgamoneyApiProperty onde for necessário carregar as configurações.
 *  **/
@EnableConfigurationProperties(AlgamoneyApiProperty.class)
public class AlgamoneyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgamoneyApiApplication.class, args);
	}
}

/** 
 * 
 * Para fazer o deploy da nossa API em Produção, será utilizado o Heroku, fazer o deploy na Nuvem. Por que o Heroku? 
 * Porque o Heroku é de graça e é muito fácil de usar, não precisa ficar configurando um monte de coisa: é só usar mesmo, a configuração é mínima.

Primeira coisa a fazer é criar a conta.
5m@NI{WyE,bwV$mum/k=Q7?E

Em Getting Started with Heroku, clicar em Java.

Vai para a página Getting Started on Heroku with Java

Maven 3 instalado na máquina local não é necessário porque o nosso projeto é com Spring Boot, que está usando o Maven Wrapper, 
Maven com w no final. Já está dentro do nosso Projeto e tem tanto para Linux/Mac quanto para Windows com cmd no final.

Clicar em I'm ready to start

Primeira coisa a se fazer, depois de criar sua conta e chegar em Set up, é instalar o Heroku CLI na máquina local.

Após instalar, vai habilitar na sua linha de comando para digitar comandos do Heroku. 

Vai para o local do projeto
C:\cd [LOCAL_DO_PROJETO]

Digitar heroku login a partir do seu terminal.

Digitar git init

Digitar git status

Criar arquivo .gitignore
Entre as coisas a serem ignoradas pelo .gitignore, ele precisa ignorar a pasta target, porque a pasta target
eh onde tem os arquivos compilados, a gente nao precisa mandar isso para o Servidor, ele vai compilar la no 
Servidor, a gente nao manda o target para o Heroku.

--- Adicionar arquivos ao controle de versão do git ---

Comando: git add .
Para adicionar todo o projeto.

Comando: git commit -m 'Primeira_Versao'
Para comitar os arquivos do projeto, adicionando a mensagem que identifica o commit.

Comando: git status
Se retornar nothing to commit, significa nada para adicionar de arquivos no controle de versão do git.

Toda vez que fizer alguma alteracao e quiser colocar em Producao, precisa desses comandos:
1. git add .
2. git commit -m 'DESCRICAO'
3. Agora, a gente pode enviar para o heroku: git push heroku master 


--- Criar a Aplicação no Heroku ---

O nome da aplicação precisa ser único em todo o Heroku, porque vai gerar uma URL, que deve ser única, não podem haver dois nomes duplicados.
Comando: heroku create algamoney-api-sidartasilva
https://algamoney-api-sidartasilva.herokuapp.com/ | https://git.heroku.com/algamoney-api-sidartasilva.git


Provisionar MySQL: addon do Heroku: JawsDB MySQL para esta aplicação. É de graça.
heroku addons:create jawsdb

As informações de conexão do MySQL estão disponíveis na variável JAWSDB_URL do Heroku:
heroku config:get JAWSDB_URL

mysql://o72o7aziiv25m03y:y4gtqyqnt90o756x@s554ongw9quh1xjs.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/chplwiqwqoub139h

Quebrar o conteúdo dessa variável em alguns valores, como, por exemplo, a URL, username, password para nós podermos configurar na nossa aplicação. 
Onde configurar? Em application-prod.properties. Em application.properties, é a configuração local. 
Em Produção, configurar no application-prod.properties através de variáveis. O nome para as variáveis pode ser qualquer um.

spring.datasource.url={JDBC_DATABASE_URL}
spring.datasource.username=JDBC_DATABASE_USERNAME
spring.datasource.password=JDBC_DATABASE_PASSWORD

As variáveis criadas no arquivo de configuração são adicionadas no heroku:

heroku config:set [NOME_VARIAVEL]=[VALOR_VARIAVEL]

heroku config:set JDBC_DATABASE_URL=jdbc:mysql://[DO_FINAL_ATE_ANTES_DO_@]

JDBC_DATABASE_USERNAME=Vai na Pagina do Heroku -> Personal -> Projeto atual -> Overview -> JawsDB MySQL -> Username

JDBC_DATABASE_PASSWORD=Vai na Pagina do Heroku -> Personal -> Projeto atual -> Overview -> JawsDB MySQL -> Password


Conferir:
heroku config


Adicionar arquivo na raiz do projeto: Procfile:
web: java -Dserver.port=$PORT -Dspring.profiles.active=oauth-security $JAVA_OPTS -jar target/algamoney*.jar

Configurações que o Heroku pede.
Ativando o profile de produção: -Dspring.profiles.active=oauth-security


git status

Adicionar modificação de arquivo mais novo arquivo criado no git:

Primeiro: git add .

git status

Então, os arquivos estão prontos para serem comitados, enviados para o controle de versão.

git commit -m 'Preparacao_Para_Producao'

Está preparado para Produção.

git status

Não tem nada para Comitar.

Enviar para o Heroku:

git push heroku master

Vai demorar um pouco: tem que provisionar a máquina, baixar as dependências.

Fez a release nessa URL:
Released v6
remote:        https://algamoney-api-sidartasilva.herokuapp.com/ deployed to Heroku

Ver o log:
heroku logs --tail

 * */
