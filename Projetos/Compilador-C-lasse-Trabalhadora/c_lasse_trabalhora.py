#!/usr/bin/env python3
import sys
import os
from sly import Lexer, Parser

class AnalisadorLexico(Lexer):
    tokens = {
        TIPO_INT, TIPO_FLOAT, TIPO_DOUBLE, TIPO_CHAR, TIPO_BOOL, TIPO_LONG,
        TIPO_SHORT, TIPO_UNSIGNED, TIPO_VOID, BOOL_TRUE, BOOL_FALSE,
        IF, ELSE, ELSEIF, WHILE, FOR, DO, SWITCH, CASE, BREAK, CONTINUE,
        PRINT,

        PROGRAMA, INICIO, FIM,
        
        ID, NUMERO, STRING,
        
        IGUAL, MAIS, MENOS, VEZES, DIVIDE, MODULO,
        IGUAL_COMP, DIFERENTE, MENOR_Q, MAIOR_Q, MENOR_IGUAL, MAIOR_IGUAL,
        E_LOGICO, OU_LOGICO, NAO_LOGICO,
    }

    ignore = ' \t'
    
    ignore_comment_line = r'//.*'
    
    @_(r'/\*(.|\n)*?\*/')
    def ignore_comment_block(self, t):
        self.lineno += t.value.count('\n')

    E_LOGICO = r'&&'
    OU_LOGICO = r'\|\|'
    NAO_LOGICO = r'!'
    
    IGUAL_COMP = r'=='
    DIFERENTE = r'!='
    MENOR_IGUAL = r'<='
    MAIOR_IGUAL = r'>='
    IGUAL = r'='
    MENOR_Q = r'<'
    MAIOR_Q = r'>'
    
    MAIS = r'\+'
    MENOS = r'-'
    VEZES = r'\*'
    DIVIDE = r'/'
    MODULO = r'%'
    
    literals = { '(', ')', '{', '}', ';', ':', ',', '.' }

    STRING = r'\"[^"]*\"'

    ID = r'[a-zA-Z_][a-zA-Z0-9_^]*'
    
    ID['formigaInteira'] = TIPO_INT
    ID['formigaFlutuante'] = TIPO_FLOAT
    ID['formigaFlutuante^2'] = TIPO_DOUBLE
    ID['formigaLetra'] = TIPO_CHAR
    ID['formigaSentinela'] = TIPO_BOOL
    ID['formigaAncia'] = TIPO_LONG
    ID['formigaLarva'] = TIPO_SHORT
    ID['operario'] = TIPO_UNSIGNED
    ID['tunelVazio'] = TIPO_VOID
    ID['vigia'] = BOOL_TRUE
    ID['descansa'] = BOOL_FALSE
    ID['seObstaculo'] = IF
    ID['senaoCavar'] = ELSE
    ID['senaoSeOutroObstaculo'] = ELSEIF
    ID['enquantoHouverComida'] = WHILE
    ID['marchar'] = FOR
    ID['cavarAteEnquanto'] = DO
    ID['inspecionarTunel'] = SWITCH
    ID['caminho'] = CASE
    ID['retornarAoNinho'] = BREAK
    ID['ignorarFolha'] = CONTINUE
    ID['sinalizar'] = PRINT
    ID['colonia'] = PROGRAMA
    ID['construir'] = INICIO
    ID['descansar'] = FIM

    @_(r'\d+[,\.]\d+|\d+')
    def NUMERO(self, t):
        t.value = t.value.replace(',', '.')
        t.value = float(t.value) if '.' in t.value else int(t.value)
        return t

    @_(r'\n+')
    def newline(self, t):
        self.lineno += len(t.value)

    def error(self, t):
        print(f"Erro Léxico: Caractere ilegal '{t.value[0]}' na linha {self.lineno}")
        self.index += 1

class GeradorCodigo(Parser):
    tokens = AnalisadorLexico.tokens

    def __init__(self):
        self.funcao_natureza_encontrada = False
        self.nome_programa = 'main'
        self.mapeamento = {
            'formigaInteira': 'int',
            'formigaFlutuante': 'float', 
            'formigaFlutuante^2': 'double',
            'formigaLetra': 'char', 
            'formigaSentinela': 'bool', 
            'formigaAncia': 'long',
            'formigaLarva': 'short', 
            'operario': 'unsigned', 
            'tunelVazio': 'void',
            'vigia': 'true', 
            'descansa': 'false', 
            'seObstaculo': 'if',
            'senaoCavar': 'else', 
            'senaoSeOutroObstaculo': 'else if',
            'enquantoHouverComida': 'while', 
            'marchar': 'for', 
            'cavarAteEnquanto': 'do',
            'inspecionarTunel': 'switch', 
            'caminho': 'case', 
            'retornarAoNinho': 'break',
            'ignorarFolha': 'continue',
        }

    @_('cabecalho_programa declaracoes')
    def programa(self, p):
        if not self.funcao_natureza_encontrada:
            raise ValueError("ERRO: Função 'natureza()' não encontrada!")
        return p.declaracoes

    @_('PROGRAMA ID ";" declaracoes')
    def programa(self, p):
        self.nome_programa = p.ID
        if not self.funcao_natureza_encontrada:
            raise ValueError("ERRO: Função 'natureza()' não encontrada!")
        return p.declaracoes

    @_('declaracoes')
    def programa(self, p):
        if not self.funcao_natureza_encontrada:
            raise ValueError("ERRO: Função 'natureza()' não encontrada!")
        return p.declaracoes

    @_('PROGRAMA ID ";"')
    def cabecalho_programa(self, p):
        self.nome_programa = p.ID
        return ''

    @_('')
    def cabecalho_programa(self, p):
        return ''

    @_('declaracao declaracoes')
    def declaracoes(self, p):
        return p.declaracao + p.declaracoes

    @_('')
    def declaracoes(self, p):
        return ''

    @_('declaracao_funcao')
    def declaracao(self, p):
        return p.declaracao_funcao

    # Declaração de função: tipo nome() { corpo }
    @_('tipo ID "(" ")" "{" corpo "}"')
    def declaracao_funcao(self, p):
        nome_funcao = p.ID
        nome_traduzido = nome_funcao
        # Função natureza → main()
        if nome_funcao == 'natureza':
            self.funcao_natureza_encontrada = True
            nome_traduzido = 'main'
        return f'{p.tipo} {nome_traduzido}() {{\n{p.corpo}}}\n'

    @_('INICIO instrucoes FIM')
    def corpo(self, p):
        return p.instrucoes

    @_('instrucoes')
    def corpo(self, p):
        return p.instrucoes

    @_('instrucao instrucoes')
    def instrucoes(self, p):
        return p.instrucao + p.instrucoes

    @_('')
    def instrucoes(self, p):
        return ''

    # Cada instrução pode ser uma variável, controle, print etc.
    @_('declaracao_variavel', 'atribuicao', 'estrutura_controle', 'break_stmt', 
       'continue_stmt', 'print_stmt', 'bloco_aninhado')
    def instrucao(self, p):
        return p[0]

    @_('INICIO instrucoes FIM')
    def bloco_aninhado(self, p):
        return f'\t{{\n{p.instrucoes}\t}}\n'

    @_('tipo ID IGUAL expressao ";"', 'tipo ID ";"')
    def declaracao_variavel(self, p):
        if len(p) == 5:
            return f'\t{p.tipo} {p.ID} = {p.expressao};\n'
        else:
            return f'\t{p.tipo} {p.ID};\n'

    @_('ID IGUAL expressao ";"')
    def atribuicao(self, p):
        return f'\t{p.ID} = {p.expressao};\n'

    @_('PRINT "(" print_arg ")" ";"')
    def print_stmt(self, p):
        argumento = p.print_arg
        if argumento.startswith('"'):
            texto_formatado = argumento[:-1] + '\\n"'
            return f'\tprintf({texto_formatado});\n'
        else:
            return f'\tprintf("%d\\n", {argumento});\n'

    @_('STRING', 'ID', 'expressao')
    def print_arg(self, p):
        return p[0]

    @_('BREAK ";"')
    def break_stmt(self, p):
        return '\tbreak;\n'

    @_('CONTINUE ";"')
    def continue_stmt(self, p):
        return '\tcontinue;\n'

    @_('if_stmt', 'while_stmt', 'for_stmt', 'do_while_stmt', 'switch_stmt')
    def estrutura_controle(self, p):
        return p[0]
        
    @_('IF "(" expressao ")" "{" corpo "}" else_parte')
    def if_stmt(self, p):
        return f'\tif ({p.expressao}) {{\n{p.corpo}\t}}{p.else_parte}\n'

    @_('ELSE "{" corpo "}"')
    def else_parte(self, p):
        return f' else {{\n{p.corpo}\t}}'

    @_('ELSEIF "(" expressao ")" "{" corpo "}" else_parte')
    def else_parte(self, p):
        return f' else if ({p.expressao}) {{\n{p.corpo}\t}}{p.else_parte}'

    @_('')
    def else_parte(self, p):
        return ''

    @_('WHILE "(" expressao ")" "{" corpo "}"')
    def while_stmt(self, p):
        return f'\twhile ({p.expressao}) {{\n{p.corpo}\t}}\n'
        
    @_('FOR "(" for_inicializacao ";" expressao ";" atribuicao_sem_ponto_virgula ")" "{" corpo "}"')
    def for_stmt(self, p):
        return f'\tfor ({p.for_inicializacao}; {p.expressao}; {p.atribuicao_sem_ponto_virgula}) {{\n{p.corpo}\t}}\n'

    @_('tipo ID IGUAL expressao', 'atribuicao_sem_ponto_virgula', '";"')
    def for_inicializacao(self, p):
        if len(p) == 4:
            return f'{p.tipo} {p.ID} = {p.expressao}'
        elif len(p) == 1 and p[0] == ';':
            return ''
        else:
            return p[0]

    @_('DO "{" corpo "}" WHILE "(" expressao ")" ";"')
    def do_while_stmt(self, p):
        return f'\tdo {{\n{p.corpo}\t}} while ({p.expressao});\n'

    @_('SWITCH "(" expressao ")" "{" case_bloco "}"')
    def switch_stmt(self, p):
        return f'\tswitch ({p.expressao}) {{\n{p.case_bloco}\t}}\n'

    @_('case_declaracao case_bloco')
    def case_bloco(self, p):
        return p.case_declaracao + p.case_bloco

    @_('')
    def case_bloco(self, p):
        return ''

    @_('CASE expressao ":" instrucoes')
    def case_declaracao(self, p):
        return f'\t\tcase {p.expressao}:\n{p.instrucoes}'

    @_('ID IGUAL expressao')
    def atribuicao_sem_ponto_virgula(self, p):
        return f'{p.ID} = {p.expressao}'

    @_('NUMERO', 'ID', 'BOOL_TRUE', 'BOOL_FALSE')
    def expressao(self, p):
        return self.mapeamento.get(p[0], str(p[0]))

    @_('expressao MAIS expressao',
       'expressao MENOS expressao',
       'expressao VEZES expressao',
       'expressao DIVIDE expressao',
       'expressao MODULO expressao',
       'expressao IGUAL_COMP expressao',
       'expressao DIFERENTE expressao',
       'expressao MENOR_Q expressao',
       'expressao MAIOR_Q expressao',
       'expressao MENOR_IGUAL expressao',
       'expressao MAIOR_IGUAL expressao',
       'expressao E_LOGICO expressao',
       'expressao OU_LOGICO expressao')
    def expressao(self, p):
        return f'{p.expressao0} {p[1]} {p.expressao1}'

    @_('NAO_LOGICO expressao', 'MENOS expressao')
    def expressao(self, p):
        return f'{p[0]}{p.expressao}'

    @_('"(" expressao ")"')
    def expressao(self, p):
        return f'({p.expressao})'

    @_('TIPO_INT', 'TIPO_FLOAT', 'TIPO_DOUBLE', 'TIPO_CHAR', 'TIPO_BOOL',
       'TIPO_LONG', 'TIPO_SHORT', 'TIPO_UNSIGNED', 'TIPO_VOID')
    def tipo(self, p):
        return self.mapeamento[p[0]]

    def error(self, p):
        if p:
            print(f"Erro de Sintaxe: Token inesperado '{p.value}' na linha {p.lineno}")
        else:
            print("Erro de Sintaxe: Fim inesperado do arquivo.")

def main():
    if len(sys.argv) < 2:
        print("Uso: python c_lasse_trabalhadora_v2.py <arquivo.formiga>")
        sys.exit(1)

    arquivo_entrada = sys.argv[1]
    if not os.path.exists(arquivo_entrada):
        print(f"Erro: Arquivo não encontrado: {arquivo_entrada}")
        sys.exit(1)

    with open(arquivo_entrada, "r", encoding="utf-8") as f:
        codigo_formiga = f.read()

    analisador_lexico = AnalisadorLexico()
    gerador_codigo = GeradorCodigo()

    try:
        tokens = analisador_lexico.tokenize(codigo_formiga)
        codigo_c = gerador_codigo.parse(tokens)

        arquivo_saida = arquivo_entrada.replace(".formiga", ".c")
        with open(arquivo_saida, "w", encoding="utf-8") as f:
            f.write(f"// Programa: {gerador_codigo.nome_programa}\n")
            f.write("#include <stdio.h>\n")
            f.write("#include <stdbool.h>\n\n")
            f.write(codigo_c)

        print(f"Compilação concluída! Arquivo C gerado: {arquivo_saida}")

    except (ValueError, TypeError) as e:
        print(e)
        sys.exit(1)

if __name__ == "__main__":
    main()