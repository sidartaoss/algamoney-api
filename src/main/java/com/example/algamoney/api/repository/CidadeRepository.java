/**
 * 
 */
package com.example.algamoney.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Cidade;

/**
 * @author sosilva
 *
 *         Aula 24.02. Criando Pesquisa de Estados e Cidades
 *
 *         Nesta aula aqui, a gente vai construir os controladores de Cidade e
 *         Estado, a gente via construir CidadeResource, EstadoResource, para
 *         podermos pesquisar pelos Estados e pelas cidades.
 *
 *         A gente vai acabar criando, tambem, um Repositorio de cada um para a
 *         gente poder dar o suporte para os nossos clientes poderem buscar as
 *         cidades e os estados.
 *
 *         1. Nos vamos, entao, abrir o pacote .repository, vamos criar, aqui,
 *         CidadeRepository e EstadoRepository.
 * 
 *         2. Agora, criar a interface EstadoRepository.
 * 
 *         3. Agora, fazer as interfaces CidadeRepository e EstadoRepository
 *         estenderem JpaRepository: JpaRepository<Cidade, Long>. Long corresponde ao tipo da nossa chave primaria.
 *         
 *         4. Vamos construir, agora, os Recursos, na pasta .resource. Primeiramente, o Recurso de Estado.
 *         Ao pesquisar, vamos devolver todos os estados.
 *         
 *         5. Ver EstadoResource.java.
 *         
 *         14. Criar o metodo findByEstadoCodigo(), passando o codigo do Estado como parametro.
 *         
 *         15. Recordando que, aqui, nos criamos uma Pesquisa a partir da assinatura de metodos, um Recurso do JPA. A parte de findBy eh como
 *         se fosse um prefixo do que a gente define. Estado eh a propriedade a qual queremos buscar. Como temos o codigo do estado, eh como 
 *         se estivessemos definindo Estado.codigo, so que, em assinaturas de metodos, a gente nao faz isso, a gente nao usa o ponto, a gente usa o 
 *         CamelCase. Entao, nos estamos buscando pelo codigo do Estado. Entao, nos passamos o codigo do Estado como parametro e a gente vai
 *         criar a lista de cidades daquele Estado passado como parametro.
 *         Voltar para CidadeResource.
 *
 */
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

	List<Cidade> findByEstadoCodigo(Long estadoCodigo);
}
