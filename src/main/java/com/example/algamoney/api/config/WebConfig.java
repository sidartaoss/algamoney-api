/**
 * 
 */
package com.example.algamoney.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author SEMPR
 *
 */
/**
 *  Aula 22.15. Criando um Agendamento de Tarefa (Scheduler)
 *  
		 *     Ainda precisamos fazer mais uma coisa: a anotacao @Scheculed(fixedDelay = 1000 * 2) eh a unica coisa que precisamos definir
		 *     para agendar. So que nos precisamos habilitar esse agendamento. Para tanto, devemos criar uma Classe de Configuracao.
		 *     Inclusive poderiamos utilizar as Classes de Configuracao AuthrorizationServerConfig, BasicSecurityConfig, ResourceServerConfig,
		 *     mas nao faz sentido definir o Scheduling nessas Classes. Entao, vamos criar outra Classe especifica para o Scheduling.
		 *     
		 *     Vamos chama-la de WebConfig e vamos cria-la dentro do pacote config.
		 *
 *	A primeira coisa que iremos fazer eh anotar a Classe como @Configuration.
 * A segunda coisa eh habilitar o Scheduling. A anotacao @EnableScheduling habilita o Scheduling. Nao eh necessario definir mais nada. Agora
 * eh so levantar a Servidor da Aplicacao e verificar o metodo anotado com @Scheduled sendo executado a cada 2 segundos.
 * Lembrando que os 2 segundos so comecam a contar quando a execucao anterior se encerra.
 */
@Configuration
@EnableScheduling
public class WebConfig {

}
