"""
Menu interativo para executar as questoes da prova.
Fica em loop ate o usuario escolher sair.
"""

import subprocess
import sys
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent
QUESTOES = {
    "1": "questao1.py",
    "2": "questao2.py",
    "3": "questao3.py",
    "4": "questao4.py",
    "5": "questao5.py",
}


def executar_questao(numero):
    arquivo = BASE_DIR / QUESTOES[numero]
    print(f"\nExecutando questao {numero} ({arquivo.name})...\n")
    try:
        subprocess.run([sys.executable, str(arquivo)], check=False)
    except Exception as exc:
        print(f"Erro ao executar a questao {numero}: {exc}")


def mostrar_menu():
    print("\n=== MENU PROVA 3 ===")
    print("1 - Questao 1")
    print("2 - Questao 2")
    print("3 - Questao 3")
    print("4 - Questao 4")
    print("5 - Questao 5")
    print("0 - Sair")


def main():
    while True:
        mostrar_menu()
        escolha = input("Escolha uma opcao: ").strip()

        if escolha == "0":
            print("Saindo. Ate mais!")
            break

        if escolha in QUESTOES:
            executar_questao(escolha)
            input("\nPressione Enter para voltar ao menu...")
        else:
            print("Opcao invalida. Tente novamente.")


if __name__ == "__main__":
    main()
