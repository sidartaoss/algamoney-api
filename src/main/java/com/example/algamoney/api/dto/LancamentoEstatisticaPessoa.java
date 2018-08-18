/**
 * 
 */
package com.example.algamoney.api.dto;

import java.math.BigDecimal;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.model.TipoLancamento;

/**
 * Aula 22.6. Instalando o Jaspersoft Studio. Implementando um relatorio Jaspersoft na API.

Nesta aula, nos vamos comecar a implementar um relatorio utilizando Jaspersoft, um relatorio na nossa API. 
Essa funcionalidade tem algumas etapas.

Primeiramente, o que nos vamos fazer eh a criacao do relatorio, nos vamos configurar o relatorio no Jaspersoft. Depois, nos vamos criar a nossa pesquisa. 
Essa pesquisa vai ficar na Classe LancamentoRepository, que sera muito parecida com a Pesquisa de Estatistica. Em seguida, criaremos um metodo no nosso
 Controlador para poder devolver esse Recurso que, nesse caso, sera o Relatorio em formato PDF. Entao, nos temos esses passos para podermos implementar
  essa funcionalidade e iremos dividir isso em algumas aulas.

Falando, agora, sobre o relatorio. O que esse relatorio vai mostrar? Ele eh um Relatorio de Lancamentos Por Pessoa e ele ira funcionar por Periodo, ou seja, 
data de inicio e fim. Ele vai nos mostrar quanto que a gente gastou ou quanto que a gente recebeu de determinada Pessoa e Por Periodo.

Nesta aula, alem dessa pequena introducao sobre a funcionalidade, vamos mostrar como instalar o Jaspersoft. Basicamente, de onde baixar e como instalar, 
apesar de ser muito simples a instalacao.

Vamos abrir o Browser para mostrar de onde baixar.

Acessar https://community.jaspersoft.com/download

Vamos escolher a versao Community Edition. Clicar em Download Now. Escolher a opcao Jaspersoft Studio para Download. Escolher a versao compativel com o 
Sistema Operacional. Baixar a versao .EXE.




Aula 22.7. Ajustando o Layout do Relatorio.

Vamos construir o nosso Relatorio. Nesta aula, nos preocuparemos mais com a parte visual, com a formatacao do nosso Relatorio e, na proxima aula, 
a gente prepara, de fato, o nosso Relatorio para receber os dados na nossa Aplicacao Java, na nossa API.

Para comecar, vamos na Aba Project Explorer e ali ja temos um Projeto criado pelo proprio Jaspersoft. Nos vamos utilizar esse mesmo projeto, 
vamos clicar com o botao direito nele e vamos na Opcao New -> Jasper Report. No Wizard, vamos selecionar a Opcao Blank A4, Next. O nome do 
Relatorio sera lancamentos-por-pessoa, Next. Vamos utilizar o Data Adapter de registros vazios, so para termos uma nocao de como esta ficando o 
nosso Relatorio, Next, Finish.

Na Area de Main Report, temos o nosso Relatorio, onde definimos a formatacao. Na Area de Palette, nos temos os componentes que iremos utilizar. 
Na Area de Outline, temos as Bandas do nosso Relatorio (de Title ate Background), os Campos (em Fields) que a gente vai definir mais para a frente, 
Parametros (em Parameters) tambem que a gente criar, serao dois Parametros: Parametro de Data de Inicio e de Fim. Quando selecionamos um elemento 
da Paleta, na Area de Propriedades (em Properties) vao ficar as propriedades desse elemento para a gente poder customiza-lo. 

Um aviso: esta aula nao eh para se especializar em Jaspersoft. Nos vamos construir o Relatorio e vamos mostrar como se utilizar os recursos necessarios 
para a construcao do nosso Relatorio. Se quisermos aprender mais sobre Jaspersoft, com o que sera ensinado nesta aula, ja da para fazermos muita coisa 
e muitas coisas ja ficarao intuitivas. A partir deste relatorio, sera possivel construir relatorios mais avancados, mas o intuito desta aula nao eh se 
especializar em Jaspersoft.

A primeira coisa que iremos fazer eh, nas Bandas (de Title a Background), deletar todas as Bandas que nao queremos. Repare-se que temos uma opcao 
para cada Banda do Relatorio que esta sendo apresentado na Area Main Report. Para cada opcao de Banda no Relatorio, temos uma linha entre as 
Bandas selecionadas na Area Outline. Vamos deletar todas as Bandas que a gente nao vai utilizar, PageHeader, por exemplo, nao iremos utilizar 
(botao Direito -> Delete). Column Header sim. Detail sim. Column Footer nao, entao iremos deletar. Page Footer sim, porque vai ser onde iremos definir 
o Numero da Pagina. Summary nao, entao iremos deletar. Background tambem nao, entao iremos remover.

Primeiro Componente do nosso Relatorio. Vamos pegar um Static Text em Palette e vamos mover para a Banda Title e vamos definir o titulo do nosso 
Relatorio. Vamos chamar de Lançamentos por Pessoa. Vamos aumentar a largura, width: 390px, height: 30px. Location x (left): 60px, y (top): 10px. 
Em seguida, vamos diminuir o tamanho da Banda Title. Selecionar em Outline -> Title, em Properties, definir em Properties -> Height: 60px. Depois, 
selecionar o Componente Static Text, definir, em Properties, na Aba Static Text, Text Alignment como Center e Middle. Clicar com o Botao direito 
no Componente Static Text e selecionar Align in Container -> Center. Definir a fonte size como 22.

Agora, vamos mexer na Banda Column Header, que eh onde vai ficar o Cabecalho da nossa Tabela, porque o Relatorio que nos vamos fazer eh aquele 
Estilo Tabela, onde vai ser definido o Cabecalho e, depois, as linhas da nossa Tabela. Vamos definir, no Column Header, um Retangulo, que fica na 
Aba Palette, em Basic Elements: clicar e arrastar ate Column Header. Primeira coisa que vamos fazer eh configurar esse Retangulo. Selecionamos, 
em Outline, o Retangulo (dentro da Banda Column Header). Na Aba Properties, a gente vai configurar Location x (left): 0, y (top): 0. O tamanho do 
Retangulo: height: 25px. E a largura? A largura nos ja sabemos que a largura total do nosso Relatorio eh 555px, entao, vamos definir com esse valor. 
Outra coisa que nos vamos mudar eh a cor de fundo do Retangulo, na Aba Appearance -> Backcolor. Vamos selecionar um tom de cinza para dar um 
destaque para o nosso Cabecalho: Hex: #D4D4D4. 

Reparemos que temos a Aba Preview para vermos como esta ficando o Relatorio.

Agora, nos vamos ajustar as Colunas que nos teremos. As colunas sao: Tipo, Pessoa e o valor, que chamaremos de Total, porque eh um somatorio 
dos gastos por Pessoa. A primeira coisa que faremos eh dividir as colunas. Para dividirmos as colunas, nos vamos utilizar o Componente Line como 
barra. Arrastar o Componente Line da Area Palette para a Banda Column Header. Dar um zoom de 125%. Note-se que o Componente Line fica na 
diagonal. Para transormarmos em uma linha totalmente na vertical, eh so diminuir o tamanho da largura ate o minimo: 1px apenas. E para ajustar a 
posicao dela no Relatorio, selecionamos-na, em Properties, Location x (left): 80px, que vai definir o tamanho da primeira coluna, e y (top): 0px. 
Definir altura, height: 25px, que eh a mesma algura do Retangulo.

Agora, nos vamos arrastar um outro Componente Line para criarmos uma terceira coluna que, no caso, ja vai criar duas colunas de uma vez, que 
eh a Coluna de Pessoa e a Coluna Total. Definir mesmas configuracoes da Linha anterior. Em Location x (left): 480px, y (top): 0px.

Verificar em Preview como esta ficando.

Voltando para a Aba Design, vamos definir os titulos das colunas. Vamos selecionar o Componente Static Field e mover para a Banda Column 
Header. Definir Text como Tipo para a primeira Coluna. Vamos configurar o alinhamento. Na Aba Properties -> Appearance, definir Size 
width: 80px, algura height: 23px, porque vamos considerar o espacamento de 1px da borda do topo e da borda inferior. Em Location, x (left): 1px, 
porque tem que considerar 1px de espacamento da borda esquerda. Em y (top): 1px, porque tem que considerar o espacamento de 1px da borda do 
topo. Agora, vamos alinhar o texto. Selecionar, na Aba Properties -> Static Text, Text Alignment como Center e Middle. 

Vamos conferir na linha vertical da Coluna Tipo qual que eh a posicao x (left) dela, que eh 80px para verificarmos se o tamanho do Tipo nao esta 
passando em relacao à linha vertical. Assim, selecionar o Componente Tipo em Outline, em Properties -> Appearance, como esta definido 1 px 
em x (left), entao vamos definir 79px em width.

Selecionar um novo Componente Static Field e arrastar para a Banda Column Header. Dois cliques no Componente e definir o texto como Pessoa. 
Para formata-lo, na Aba Properties -> Static Text, alinhar Center e Middle. Ir em Properties -> Appearance para definir o tamanho: height = 25px. 
Ajustar Location y (top) para 1 para tirar 1px da borda de cima. Estender o Componente até o limite dos Campos Tipo e Total.

Copiar/Colar o Campo Tipo e Formatar para o Campo Total.

Agora, vamos definir onde ficarao as linhas. Selecionar a Banda Detail, que onde os elementos vao se repetir. Eh na Detail onde serao exibidos os 
dados que a gente passar para o nosso Relatorio. Serao utilizados os Componentes Line para marcar-se os cantos e separar-se as Colunas. 
Arrastar o Componente Line para Column Header. Definir width: 1px. Alterar o tamanho, height: 25px. Em Location, y (top): 0px. Em x (left): 0px. 
Em Outline, selecionar a linha da Banda Column Header e mover para a Banda Detail.

Como a Banda Column Header esta muito grande, diminuir, selecionando em Outline -> Column Header, na Aba Properties -> Appearance, em Height: 26px.

Selecionar a Linha na Banda Detail.

Linha Vertical Esquerda: Definir Location x (left): 0px, y (top): 0px.

Linha Vertical Coluna Pessoa: Copiar/Colar a Linha Vertical da Coluna Tipo. Mover para a Banda Detail. Definir Location x (left): 80px, y (top): 0px.

Linha Vertical Coluna Total: Copiar/Colar a Linha Vertical Coluna Pessoa. Definir Location x (left): 455px, y (top): 0px.

Linha Vertical Direita: Copiar/Colar a Linha Vertical Coluna Total. Definir Location x (left): 555px, y (top): 0px.

Agora, esta faltando uma linha para fechar a parte de baixo do nosso Relatorio. Arrastar mais um Componente Line para a Banda Detail. 
Vamos definir sua Largura como 555px, Aba Properties -> Appearance -> Size -> Width: 555px, Height: 1px. Em Location, x (left): 0px, y (top): 24px 
(24px para nao estourar a altura da Banda Detail, que sera definido como 25px).

Selecionar a Banda Detail em Outline, Properties -> Appearance -> Height: 25px.

Na proxima aula, nos vamos preparar o nosso Relatorio para receber as informacoes da nossa Aplicacao.



Aula 22.8. Criando o DTO do Relatorio.

Agora, nos estamos com o STS aberto, ja nao estamos mais com o Jaspersoft aberto. Por quê? Na proxima aula que formos mexer com Jaspersoft, 
nos vamos fazer a preparacao do Relatorio para receber os dados da nossa Aplicacao. O que o Relatorio vai receber, na pratica, eh uma lista de um 
objeto DTO. Entao, vamos criar esse objeto, primeiramente, porque, quando vermos essa Classe que iremos criar e for fazer isso no Relatorio, 
as coisas vao ficar mais claras.

Ir no pacote dto e criar a Classe LancamentoEstatisticaPessoa.

Essa Classe vai ter tres atributos, que sao: o Tipo do Lancamento, a Pessoa e o Total.

Criar um Construtor e remover super().

Criar Getters and Setters.

Total eh a propriedade que vai receber o resultado do SUM(). Em TipoLancamento, dentro do Enum, definir uma String para cada Enum. Ver no
Enum TipoLancamento. 

 */

/**
 * @author SEMPR
 *
 */
public class LancamentoEstatisticaPessoa {

	private TipoLancamento tipo;
	private Pessoa pessoa;
	private BigDecimal total;

	/**
	 * @param tipo
	 * @param pessoa
	 * @param total
	 */
	public LancamentoEstatisticaPessoa(TipoLancamento tipo, Pessoa pessoa, BigDecimal total) {
		this.tipo = tipo;
		this.pessoa = pessoa;
		this.total = total;
	}
	
	/**
	 * @return the tipo
	 */
	public TipoLancamento getTipo() {
		return tipo;
	}


	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the pessoa
	 */
	public Pessoa getPessoa() {
		return pessoa;
	}

	/**
	 * @param pessoa the pessoa to set
	 */
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
