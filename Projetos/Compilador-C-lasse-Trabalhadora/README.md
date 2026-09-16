# Linguagem C-lasse Trabalhadora

Bem-vindo ao repositório da **C-lasse Trabalhadora**, uma linguagem de programação experimental e temática, inspirada no universo das formigas e sua sociedade organizada. Esta linguagem foi criada como um projeto educacional para demonstrar os conceitos de análise léxica e sintática, culminando em um compilador que traduz o código-fonte para a linguagem C.

## 🐜 Sobre o Conceito

A C-lasse Trabalhadora reimagina a sintaxe da programação em linguagem C com um vocabulário que remete ao trabalho árduo e coordenado de uma colônia de formigas. Conceitos como tipos de dados, estruturas de controle e funções são representados por termos como `formigaInteira`, `seObstaculo`, e `natureza`.

O objetivo é criar uma experiência de programação divertida e didática, onde cada linha de código contribui para a construção de um "formigueiro" lógico.

## ⚙️ Como Funciona

O compilador foi desenvolvido em Python utilizando a biblioteca `sly` para a criação do analisador léxico (lexer) e do analisador sintático (parser). O processo ocorre em duas etapas principais:

1.  **Análise Léxica**: O código-fonte em um arquivo `.formiga` é lido e dividido em "tokens" (as menores unidades lógicas da linguagem, como palavras-chave, identificadores e operadores).
2.  **Análise Sintática e Tradução**: Os tokens são analisados para verificar se a estrutura do código segue as regras gramaticais da linguagem. Se a sintaxe estiver correta, o parser constrói o código equivalente em linguagem C.

O resultado final é um arquivo `.c` que pode ser compilado por qualquer compilador C padrão (como o GCC). O compilador automaticamente inclui os cabeçalhos `<stdio.h>` e `<stdbool.h>` no arquivo de saída.

## 📋 Tabela de Palavras-Chave

A tabela abaixo mostra a correspondência entre as palavras-chave da C-lasse Trabalhadora e seus equivalentes em C.

| **C-lasse Trabalhadora** | **Equivalente em C** | **Descrição** |
| ------------------------- | -------------------- | ------------------------------------------ |
| `natureza`                | `main`               | Função principal do programa               |
| `sinalizar`               | `printf`             | Função para imprimir texto ou variáveis    |
| `formigaInteira`          | `int`                | Tipo de dado para números inteiros         |
| `formigaFlutuante`        | `float`              | Tipo para números de ponto flutuante       |
| `formigaFlutuante^2`      | `double`             | Tipo para ponto flutuante de precisão dupla |
| `formigaLetra`            | `char`               | Tipo para um único caractere               |
| `formigaSentinela`        | `bool`               | Tipo booleano (verdadeiro/falso)           |
| `formigaAncia`            | `long`               | Modificador de tipo `long`                 |
| `formigaLarva`            | `short`              | Modificador de tipo `short`                |
| `operario`                | `unsigned`           | Modificador de tipo `unsigned`             |
| `tunelVazio`              | `void`               | Tipo para funções sem retorno              |
| `vigia`                   | `true`               | Valor booleano verdadeiro                  |
| `descansa`                | `false`              | Valor booleano falso                       |
| `seObstaculo`             | `if`                 | Estrutura de decisão condicional           |
| `senaoCavar`              | `else`               | Bloco alternativo para o `if`              |
| `senaoSeOutroObstaculo`   | `else if`            | Condicional alternativa                    |
| `enquantoHouverComida`    | `while`              | Laço de repetição `while`                  |
| `marchar`                 | `for`                | Laço de repetição `for`                    |
| `cavarAteEnquanto`        | `do`                 | Estrutura de laço `do-while`               |
| `inspecionarTunel`        | `switch`             | Estrutura de seleção `switch`              |
| `caminho`                 | `case`               | Rótulo de caso dentro de um `switch`       |
| `retornarAoNinho`         | `break`              | Sai de um laço ou `switch`                 |
| `ignorarFolha`            | `continue`           | Pula para a próxima iteração do laço       |

## 🚀 Como Usar

### Pré-requisitos

-   Python 3
-   Biblioteca SLY. Para instalá-la, execute:
    ```bash
    pip install sly
    ```

### Execução

Para compilar um arquivo escrito em C-lasse Trabalhadora, utilize o seguinte comando no seu terminal:

```bash
python c_lasse_trabalhadora.py seu_arquivo.formiga
````

Isso irá gerar um arquivo C chamado `seu_arquivo.c` no mesmo diretório.

### Exemplo de Código

Crie um arquivo chamado `exemplo.formiga` com o seguinte conteúdo:

```
// exemplo.formiga
// Este programa imprime números de 0 a 4, marchando como formigas.

tunelVazio natureza() {
    formigaInteira contador;

    marchar(contador = 0; contador < 5; contador = contador + 1) {
        sinalizar("Formiga marchando, passo:");
        sinalizar(contador);
    }
}
```

Execute o compilador:

```bash
python c_lasse_trabalhadora.py exemplo.formiga
```

O arquivo `exemplo.c` será gerado com o seguinte código:

```c
// exemplo.c
#include <stdio.h>
#include <stdbool.h>

void main() {
	int contador;
	for (contador = 0; contador < 5; contador = contador + 1) {
		printf("Formiga marchando, passo:\n");
		printf("%d\n", contador);
	}
}
```
