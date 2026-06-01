# Como instalar
Faça o download do programa na seção **Releases**, no menu lateral direito. Para baixar o programa basta fazer o download do arquivo Java (com extensão `.jar`).
> O programa foi criado utilizando a linguagem de programação Java. Portanto, para executá-lo no seu computador, é necessário que você instale o executor Java. Caso ainda não o tenha instalado, [clique aqui](https://www.java.com/pt-br/download/manual.jsp) e siga as instruções de instalação.

# Como utilizar
O programa foi desenhado para levantar documentos do [Diário Oficial da União (DOU)](https://dados.gov.br/dados/busca?termo=di%25C3%25A1rio%2520oficial%2520da%2520uni%25C3%25A3o). 

> A rotina de programação desenvolvida pode ser utilizada para pesquisas que, utilizando o DOU como fonte de informações, tenham distintos objetos e objetivos, pois a seleção dos arquivos pertinentes acontece por meio de termos de busca que podem ser livremente modificados.

Para utilizar o programa é necessário que você faça o download dos arquivos do DOUem seu computador. Estes arquivos são disponibilizados em três seções, separados por ano e mês. Na seção 1, encontram-se publicados atos normativos de interesse geral (leis, decretos, resoluções, instruções normativas, portarias e outros). Na seção 02, atos relativos aos servidores públicos e, na seção 3, extratos de instrumentos contratuais, intimação, notificação e concursos públicos, comunicados, avisos de licitação, dentre outros atos da administração pública decorrentes de disposição legal.

> Os arquivos são obtidos no formato XML – sigla para Extensible Markup Language, que significa Linguagem de Marcação Extensível. O XML é um formato de arquivo que permite a definição e armazenamento de dados de forma organizada, utilizando uma codificação de documentos que garante que o arquivo final seja legível tanto para os usuários quanto para os computadores.

## Download dos arquivos
Acesse o [Diário Oficial da União (DOU)](https://dados.gov.br/dados/busca?termo=di%25C3%25A1rio%2520oficial%2520da%2520uni%25C3%25A3o), identifique o ano de interesse e vá até a seção **Recursos**. Para cada mês desejado, clique em **Acessar o recurso** para fazer download dos arquivos XML em pasta compactada ZIP.

> Caso não seja possível fazer o download do arquivo pelos navegadores Chrome, Firefox, Opera e afins, acesso o link pelo navegador padrão do Windows, Microsoft Edge.

## Extração dos arquivos XML
O download é feito em formato compactado ZIP. Para acessar os arquivos XML, é necessário fazer sua extração. 

> Recomenda-se a utilização do software [7Zip](https://www.7-zip.org/), para uma extração mais rápida. Porém, é possível utilizar a ferramenta nativa do Windows.

Vá até a pasta Downloads, clique com o botão direito no arquivo `.zip` baixado e clique em **Extrair tudo**. Selecione a pasta de destino, ou utilize a pasta sugerida, e clique em **Extrair**.

> Caso opte por utilizar o software 7Zip, o caminho correto é clicar em **Mostrar mais opções**, ir até a opção **7Zip** e clicar em **Extrair em `NomeDaPasta`**
## Organização dos arquivos em pastas
Para que o programa consiga ler os arquivos baixados, é necessário organizá-los na seguinte estrutura:

- uma pasta geral, que irá conter todos os arquivos XML baixados. Esta será a pasta utilizada pelo programa, no campo **Diretório contendo arquivos para pesquisa**;
- dentro da pasta geral, deve-se ter uma pasta por ano;
- dentro da pasta de cada ano, deve-se ter uma pasta por mês.

Crie uma pasta geral e uma pasta por ano no local do seu computador em que serão armazenados os arquivos XML. Após download e extração de cada ano e mês, arraste a pasta extraída para a pasta criada do seu ano correspondente.

Pasta geral:
![pasta geral](https://i.imgur.com/8S4IWQw.png)

Pastas por ano (contida na pasta geral):
![pasta por ano](https://i.imgur.com/eU1Rnry.png)

Pastas por mês (contidas nas pastas dos seus respectivos anos):
![pasta por mês](https://i.imgur.com/WzwZGx8.png)

![pasta por mês](https://i.imgur.com/FfGsNni.png)

Feito isso, os arquivos estão prontos para serem acessados pelo programa.

## Execução do programa
Crie uma pasta específica para rodar o programa. Mova o arquivo Java (extensão `.jar`) baixado para esta pasta e execute-o.

> O resultado do programa é gerado na mesma pasta em que o arquivo Java está localizado. Por isso, não é recomendado deixar o arquivo `.jar` na pasta **Downloads** do seu computador.

Após execução, abrirá a tela abaixo:
![tela inicial do programa](https://i.imgur.com/SGHDpJb.png)

Para executar a busca:
- preencha as palavras-chave que serão utilizadas para pesquisa no corpo dos arquivos; 
- preencha o(s) órgão(s) responsável(s) ou deixe em branco para buscar em todos os órgãos do governo executivo federal;
- informe a pasta geral criada anteriormente, clicando em **Procurar**;
- clique em **Executar**.

> Caso as palavras-chave buscadas contenham caracteres especiais (`ç`, `ã`, `á`, etc), substitua-os pelo caractere ponto final `.`. Ex.: `atenção básica` -> `aten..o b.sica`.

## Resultado da busca
Os resultados encontrados serão disponibilizados em planilhas Excel, localizadas na mesma pasta em que o programa se encontra. Cada ano corresponderá a uma planilha. Cada planilha conterá uma aba por mês, além de uma aba geral **Totais**, contendo o resultado anual.
